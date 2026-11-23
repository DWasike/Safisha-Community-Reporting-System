
package com.example.safisha.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safisha.R
import com.example.safisha.domain.model.ReportStatus
import com.example.safisha.ui.adapter.ReportAdapter
import com.example.safisha.ui.viewmodel.ReportFilter
import com.example.safisha.ui.viewmodel.SafishaViewModel
import com.example.safisha.ui.viewmodel.UiEvent
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.example.safisha.domain.model.UserRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFragment : Fragment() {

    private val viewModel: SafishaViewModel by activityViewModels()

    private lateinit var reportAdapter: ReportAdapter
    private lateinit var recyclerReports: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvError: TextView
    private lateinit var filterContainer: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupFilters()
        observeReports()
        observeEvents()
    }

    private fun initializeViews(view: View) {
        recyclerReports = view.findViewById(R.id.recyclerReports)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        tvError = view.findViewById(R.id.tvError)
        filterContainer = view.findViewById(R.id.filterContainer)
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter { report ->
            showReportDetails(report)
        }

        recyclerReports.layoutManager = LinearLayoutManager(requireContext())
        recyclerReports.adapter = reportAdapter
    }

    private fun setupFilters() {
        val isAdmin = arguments?.getBoolean("showAll") ?: false

        // Residents only see their own reports and do not need admin filters.
        filterContainer.visibility = if (isAdmin) View.VISIBLE else View.GONE

        view?.findViewById<Button>(R.id.btnAll)?.setOnClickListener {
            viewModel.setFilter(ReportFilter.All)
        }

        view?.findViewById<Button>(R.id.btnPending)?.setOnClickListener {
            viewModel.setFilter(ReportFilter.ByStatus(ReportStatus.PENDING))
        }

        view?.findViewById<Button>(R.id.btnInProgress)?.setOnClickListener {
            viewModel.setFilter(ReportFilter.ByStatus(ReportStatus.IN_PROGRESS))
        }

        view?.findViewById<Button>(R.id.btnResolved)?.setOnClickListener {
            viewModel.setFilter(ReportFilter.ByStatus(ReportStatus.RESOLVED))
        }
    }

    private fun observeReports() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reportsState.collect { state ->
                    progressBar.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE

                    tvEmpty.visibility =
                        if (state.isEmpty) View.VISIBLE else View.GONE

                    tvError.visibility =
                        if (state.errorMessage != null) View.VISIBLE else View.GONE

                    recyclerReports.visibility =
                        if (!state.isLoading &&
                            state.errorMessage == null &&
                            state.reports.isNotEmpty()
                        ) View.VISIBLE else View.GONE

                    tvError.text = state.errorMessage ?: ""
                    reportAdapter.submitList(state.reports)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.Message -> {
                            android.widget.Toast.makeText(
                                requireContext(),
                                event.text,
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showReportDetails(
        report: com.example.safisha.data.local.entity.IssueReportEntity
    ) {
        val isAdmin = viewModel.currentUser.value?.role == UserRole.ADMIN

        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        )

        val details = """
        Report ID: ${report.id}

        Category: ${report.categoryId}

        Location: ${report.location}

        Description: ${report.description}

        Reported by: ${report.reporterName}

        Date: ${dateFormat.format(Date(report.timestamp))}

        Current Status: ${report.status}
    """.trimIndent()

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Report Details")
            .setMessage(details)
            .setNeutralButton("Close", null)

        if (isAdmin) {
            if (report.status != ReportStatus.RESOLVED) {
                builder.setPositiveButton("Update Status") { _, _ ->
                    viewModel.advanceStatus(report)
                }
            } else {
                builder.setNegativeButton("Delete Report") { _, _ ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this resolved report?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete") { _, _ ->
                            viewModel.deleteReport(report)
                        }
                        .show()
                }
            }
        }

        builder.show()
    }



}
