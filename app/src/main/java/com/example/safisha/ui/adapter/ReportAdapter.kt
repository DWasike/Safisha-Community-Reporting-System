
package com.example.safisha.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.safisha.R
import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.domain.model.ReportStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportAdapter(
    private val onReportClick: (IssueReportEntity) -> Unit
) : ListAdapter<IssueReportEntity, ReportAdapter.ReportViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)

        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReportViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvCategory: TextView =
            itemView.findViewById(R.id.tvCategory)

        private val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)

        private val tvLocation: TextView =
            itemView.findViewById(R.id.tvLocation)

        private val tvDescription: TextView =
            itemView.findViewById(R.id.tvDescription)

        private val tvReporter: TextView =
            itemView.findViewById(R.id.tvReporter)

        private val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        fun bind(report: IssueReportEntity) {

            tvCategory.text = report.categoryId
            tvLocation.text = "Location: ${report.location}"
            tvDescription.text = report.description
            tvReporter.text = "Reported by: ${report.reporterName}"

            val dateFormat = SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

            tvDate.text = "Date: ${
                dateFormat.format(Date(report.timestamp))
            }"

            when (report.status) {

                ReportStatus.PENDING -> {
                    tvStatus.text = ReportStatus.PENDING
                    tvStatus.setBackgroundResource(R.drawable.bg_pending)
                    tvStatus.setTextColor(0xFF8A5700.toInt())
                }

                ReportStatus.IN_PROGRESS -> {
                    tvStatus.text = ReportStatus.IN_PROGRESS
                    tvStatus.setBackgroundResource(R.drawable.bg_in_progress)
                    tvStatus.setTextColor(0xFF1557A0.toInt())
                }

                ReportStatus.RESOLVED -> {
                    tvStatus.text = ReportStatus.RESOLVED
                    tvStatus.setBackgroundResource(R.drawable.bg_resolved)
                    tvStatus.setTextColor(0xFF176B32.toInt())
                }

                else -> {
                    tvStatus.text = report.status
                    tvStatus.setBackgroundResource(R.drawable.bg_pending)
                    tvStatus.setTextColor(0xFF8A5700.toInt())
                }
            }

            itemView.setOnClickListener {
                onReportClick(report)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<IssueReportEntity>() {

        override fun areItemsTheSame(
            oldItem: IssueReportEntity,
            newItem: IssueReportEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: IssueReportEntity,
            newItem: IssueReportEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}
