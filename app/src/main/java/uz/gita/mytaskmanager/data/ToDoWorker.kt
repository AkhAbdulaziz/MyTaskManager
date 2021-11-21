package uz.gita.mytaskmanager.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import uz.gita.mytaskmanager.R
import uz.gita.mytaskmanager.domain.AppRepository

class ToDoWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private val repository = AppRepository.getRepository()

    override fun doWork(): Result {
        if (repository.isNotificationEnabled) {
            setNotification(
                inputData.getInt("id", 200),
                inputData.getString("title") as String
            )
        }
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
            .setSmallIcon(R.drawable.app_icon)

        manager.notify(id, builder.build())
    }
}
