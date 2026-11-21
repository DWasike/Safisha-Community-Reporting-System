package com.example.safisha.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.safisha.domain.model.ReportStatus

@Entity(
    tableName = "issue_reports",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporterId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["reporterId"]),
        Index(value = ["status"]),
        Index(value = ["categoryId"])
    ]
)
data class IssueReportEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val categoryId: String,

    val location: String,

    val description: String,

    val status: String = ReportStatus.PENDING,

    val reporterName: String,

    val reporterId: Int,

    val timestamp: Long = System.currentTimeMillis()
)