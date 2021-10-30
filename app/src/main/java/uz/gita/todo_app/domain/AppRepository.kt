package uz.gita.todo_app.domain

import uz.gita.todo_app.data.database.AppDatabase
import uz.gita.todo_app.data.entity.TaskEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor() {
    private val dao = AppDatabase.getDatabase().getTaskDao()

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