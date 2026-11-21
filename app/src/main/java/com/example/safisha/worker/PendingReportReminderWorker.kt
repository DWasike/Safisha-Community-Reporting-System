package com.example.safisha.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.safisha.data.local.SafishaDatabase
import com.example.safisha.data.repository.SafishaRepository
import com.example.safisha.domain.model.ReportStatus
import kotlinx.coroutines.flow.first
import kotlin.coroutines.cancellation.CancellationException

class PendingReportReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = SafishaRepository(
                SafishaDatabase.getDatabase(applicationContext).safishaDao()
            )
            val unresolved = repository.getAllReports().first()
                .count { it.status != ReportStatus.RESOLVED }
            if (unresolved > 0) NotificationHelper.showUnresolved(applicationContext, unresolved)
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}