
package com.example.safisha.data.repository

import com.example.safisha.data.local.dao.SafishaDao
import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class SafishaRepository(
    private val safishaDao: SafishaDao
) {

    // User operations

    suspend fun registerUser(user: UserEntity): Long {
        return safishaDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return safishaDao.getUserByEmail(email)
    }

    // Report operations

    suspend fun insertReport(report: IssueReportEntity): Long {
        return safishaDao.insertReport(report)
    }

    fun getAllReports(): Flow<List<IssueReportEntity>> {
        return safishaDao.getAllReports()
    }

    fun getReportsByUser(userId: Int): Flow<List<IssueReportEntity>> {
        return safishaDao.getReportsByUser(userId)
    }

    fun getReportsByStatus(status: String): Flow<List<IssueReportEntity>> {
        return safishaDao.getReportsByStatus(status)
    }

    suspend fun updateReport(report: IssueReportEntity) {
        safishaDao.updateReport(report)
    }

    suspend fun updateReportStatus(
        reportId: Int,
        newStatus: String
    ): Int {
        return safishaDao.updateReportStatus(reportId, newStatus)
    }

    suspend fun deleteReport(report: IssueReportEntity) {
        safishaDao.deleteReport(report)
    }

    suspend fun deleteReportById(reportId: Int): Int {
        return safishaDao.deleteReportById(reportId)
    }
}
