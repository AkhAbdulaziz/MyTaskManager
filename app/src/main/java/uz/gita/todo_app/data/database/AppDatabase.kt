package uz.gita.todo_app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.gita.todo_app.app.App
import uz.gita.todo_app.data.dao.TaskDao
import uz.gita.todo_app.data.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao

    companion object {
        private lateinit var instance: AppDatabase

        fun getDatabase(): AppDatabase {
            if (!::instance.isInitialized) {
                instance =
                    Room.databaseBuilder(App.instance, AppDatabase::class.java, "Todo")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return instance
        }
    }
}