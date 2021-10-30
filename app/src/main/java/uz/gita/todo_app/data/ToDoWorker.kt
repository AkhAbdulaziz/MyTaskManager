package uz.gita.todo_app.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import uz.gita.todo_app.R

class ToDoWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        setNotification(
            inputData.getInt("id", 200),
            inputData.getString("title") as String
        )
        return Result.success()
    }

    private fun setNotification(id: Int, title: String) {
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "workmanager",
                "Work Manager",
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, "workmanager")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        manager.notify(id, builder.build())
    }
}
