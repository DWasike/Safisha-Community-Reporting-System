
package com.example.safisha.domain.model

object ReportStatus {
    const val PENDING = "Pending"
    const val IN_PROGRESS = "In Progress"
    const val RESOLVED = "Resolved"

    val all = listOf(
        PENDING,
        IN_PROGRESS,
        RESOLVED
    )
}
