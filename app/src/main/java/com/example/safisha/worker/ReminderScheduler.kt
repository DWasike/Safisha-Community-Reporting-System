package com.example.safisha.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "pending_report_reminder"

    // Android's minimum repeat interval is 15 minutes; sooo once a day is realistic
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<PendingReportReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request   // KEEP: no duplicates
        )
    }

    // For testing and the live demo:
    fun runNow(context: Context) {
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<PendingReportReminderWorker>().build())
    }
}