package uz.gita.mytaskmanager.domain

import android.content.Context
import uz.gita.mytaskmanager.app.App
import uz.gita.mytaskmanager.data.database.AppDatabase
import uz.gita.mytaskmanager.data.entity.TaskEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor() {
    private val dao = AppDatabase.getDatabase().getTaskDao()

    companion object {
        private lateinit var repository: AppRepository
        fun getRepository(): AppRepository {
            if (!Companion::repository.isInitialized) {
                repository = AppRepository()
            }
            return repository
        }
    }

    private val pref = App.instance.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)

    var isNotificationEnabled
        set(value) = pref.edit().putBoolean("NOTIFICATION_STATUS", value).apply()
        get() = pref.getBoolean("NOTIFICATION_STATUS", true)

    fun insert(data: TaskEntity) {
        dao.insert(data)
    }

    fun update(data: TaskEntity) {
        dao.update(data)
    }

    fun delete(data: TaskEntity) {
        dao.delete(data)
    }

    fun getDataByPagePos(pos: Int): List<TaskEntity> {
        return dao.getDataByPagePos(pos)
    }

    fun getAllData(): List<TaskEntity> {
        return dao.getAllData()
    }

    fun getMaxId(): Int = dao.getMaxId()!!
}