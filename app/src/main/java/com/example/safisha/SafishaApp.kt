package com.example.safisha

import android.app.Application
import com.example.safisha.worker.NotificationHelper
import com.example.safisha.worker.ReminderScheduler

class SafishaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        ReminderScheduler.schedule(this)
    }
}