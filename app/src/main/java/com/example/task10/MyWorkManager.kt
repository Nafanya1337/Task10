package com.example.task10

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class MyWorkManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            // Получаем время ожидания из inputData
            val delaySeconds = inputData.getInt("delaySeconds", 0)

            // Ожидание
            Thread.sleep(delaySeconds * 1000L)

            // Создаем и показываем уведомление
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NotificationUtils.createNotificationChannel(applicationContext)
            val builder = NotificationCompat.Builder(applicationContext, NotificationUtils.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Push Уведомление")
                .setContentText("Картинка загружена!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notification = builder.build()
            notificationManager.notify(1, notification)

            return Result.success()
        } catch (e: InterruptedException) {
            return Result.failure()
        }
    }
}