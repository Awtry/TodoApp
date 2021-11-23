package com.awtry.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationManagerImp(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        val id = inputData.getInt("notificationID", 0)
        val title = inputData.getString("notificationTitle") ?: ""
        val description = inputData.getString("notificationDescription") ?: ""

        createNotification(context, Task(id, title, description))

        return success()
    }

    /*fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TASK"
            val descriptionText = "Channel of pending tasks"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }*/

    fun createNotification(context: Context, task: Task) {

        val detailIntel = Intent(context, MainActivity::class.java).apply {
            putExtra("isTaskDetail", true)
            putExtra("task", task)
        }

        val PendingIntent: PendingIntent? = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(detailIntel)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val builder = NotificationCompat.Builder(context, "TASK_CHANNEL")
            .setSmallIcon(R.drawable.ic_clook)
            .setContentTitle(task.titulo)
            .setContentText(task.description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(PendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(task.id, builder.build())
        }
    }

}