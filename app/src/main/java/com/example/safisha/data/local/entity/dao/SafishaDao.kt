
package com.example.safisha.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SafishaDao {

    // User operations

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Report operations

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReport(report: IssueReportEntity): Long

    @Query("SELECT * FROM issue_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<IssueReportEntity>>

    @Query(
        "SELECT * FROM issue_reports " +
                "WHERE reporterId = :userId ORDER BY timestamp DESC"
    )
    fun getReportsByUser(userId: Int): Flow<List<IssueReportEntity>>

    @Query(
        "SELECT * FROM issue_reports " +
                "WHERE status = :status ORDER BY timestamp DESC"
    )
    fun getReportsByStatus(status: String): Flow<List<IssueReportEntity>>

    @Update
    suspend fun updateReport(report: IssueReportEntity)

    @Query(
        "UPDATE issue_reports SET status = :newStatus " +
                "WHERE id = :reportId"
    )
    suspend fun updateReportStatus(
        reportId: Int,
        newStatus: String
    ): Int

    @Delete
    suspend fun deleteReport(report: IssueReportEntity)

    @Query("DELETE FROM issue_reports WHERE id = :reportId")
    suspend fun deleteReportById(reportId: Int): Int
}
