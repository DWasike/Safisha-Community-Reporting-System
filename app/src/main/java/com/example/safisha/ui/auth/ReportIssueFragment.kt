package com.example.safisha.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.safisha.R
import com.example.safisha.databinding.FragmentReportIssueBinding
import com.example.safisha.domain.model.IssueCategory
import com.example.safisha.ui.viewmodel.SafishaViewModel
import com.example.safisha.ui.viewmodel.UiEvent
import kotlinx.coroutines.launch

class ReportIssueFragment : Fragment() {

    private var _binding: FragmentReportIssueBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SafishaViewModel by activityViewModels()

    private val specs = IssueCategory.specs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Category dropdown is built from the shared IssueCategory list
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            specs.map { it.displayTitle }
        )
        binding.actCategory.setAdapter(adapter)
        binding.actCategory.setOnItemClickListener { _, _, position, _ ->
            binding.tilCategory.error = null
            binding.tvCategoryHint.text = specs[position].includes
            binding.tvCategoryHint.visibility = View.VISIBLE
        }

        binding.etLocation.doOnTextChanged { _, _, _, _ -> binding.tilLocation.error = null }
        binding.etDescription.doOnTextChanged { _, _, _, _ -> binding.tilDescription.error = null }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSubmit.setOnClickListener { attemptSubmit() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    if (event is UiEvent.Message) {
                        setLoading(false)
                        Toast.makeText(requireContext(), event.text, Toast.LENGTH_LONG).show()
                        // The ViewModel confirms a save with this message
                        if (event.text == "Report submitted") {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun attemptSubmit() {
        // Work out the category from what is shown, so it also survives rotation
        val chosen = specs.firstOrNull { it.displayTitle == binding.actCategory.text.toString() }
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        val categoryError = if (chosen == null) getString(R.string.error_category_required) else null
        val locationError = when {
            location.isEmpty() -> getString(R.string.error_location_required)
            location.length < 3 -> getString(R.string.error_location_short)
            else -> null
        }
        val descriptionError = when {
            description.isEmpty() -> getString(R.string.error_description_required)
            description.length < 10 -> getString(R.string.error_description_short)
            else -> null
        }

        binding.tilCategory.error = categoryError
        binding.tilLocation.error = locationError
        binding.tilDescription.error = descriptionError

        // Block the submit until every field is valid
        if (chosen == null || locationError != null || descriptionError != null) return

        setLoading(true)
        viewModel.submitReport(chosen.id, location, description)
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}