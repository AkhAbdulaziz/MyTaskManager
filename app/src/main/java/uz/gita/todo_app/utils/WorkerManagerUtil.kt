package uz.gita.todo_app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.work.*
import uz.gita.todo_app.app.App
import uz.gita.todo_app.data.ToDoWorker
import uz.gita.todo_app.data.entity.TaskEntity
import uz.gita.todo_app.domain.AppRepository
import java.util.*
import java.util.concurrent.TimeUnit

private val repository = AppRepository()
fun cancelRequest(id: String) {
    WorkManager.getInstance(App.instance)
        .cancelWorkById(UUID.fromString(id))
}

fun recreateWorkRequest(taskData: TaskEntity): Boolean {
    createNotificationChannel()
    val calendarHelp = Calendar.getInstance()
    val day = calendarHelp.get(Calendar.DAY_OF_MONTH)
    val month = calendarHelp.get(Calendar.MONTH) + 1
    val year = calendarHelp.get(Calendar.YEAR)
    val hour = calendarHelp.get(Calendar.HOUR_OF_DAY)

    val constraints = Constraints.Builder()
        .setRequiresCharging(false)
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresBatteryNotLow(true)
        .build()

    val isDayNotPast =
        taskData.enteredYear > year || (taskData.enteredYear == year && taskData.enteredMonth > month) ||
                (taskData.enteredYear == year && taskData.enteredMonth == month && taskData.enteredDay > day) ||
                (taskData.enteredYear == year && taskData.enteredMonth == month && taskData.enteredDay == day &&
                        taskData.time.substring(0, taskData.time.indexOf(':')).trim()
                            .toInt() > hour)

    if (isDayNotPast) {
        val data = Data.Builder()
        data.putInt("id", repository.getMaxId())
        data.putString("title", taskData.task)

        val request = OneTimeWorkRequest.Builder(ToDoWorker::class.java)
            .setConstraints(constraints)
            .setInitialDelay(
                Math.abs(taskData.timeInMilliSeconds - System.currentTimeMillis()),
                TimeUnit.MILLISECONDS
            )
            .setInputData(data.build())
            .build()
        /*   Log.d(
               "FFF",
               "new initial delay ${taskData.timeInMilliSeconds - System.currentTimeMillis()}"
           )*/
        WorkManager.getInstance(App.instance).enqueue(request)
        return true
    } else {
        Toast.makeText(App.instance, "You cannot add task for past!", Toast.LENGTH_SHORT)
            .show()
        return false
    }
}


private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel("T", "Demo", NotificationManager.IMPORTANCE_DEFAULT)
        val manager =
            App.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}