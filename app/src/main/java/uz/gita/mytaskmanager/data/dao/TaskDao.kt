package uz.gita.mytaskmanager.data.dao

import androidx.room.*
import uz.gita.mytaskmanager.data.entity.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM TaskEntity ORDER BY enteredYear , enteredMonth, enteredDay")
    fun getAllData() : List<TaskEntity>

    @Query("SELECT * FROM TaskEntity WHERE TaskEntity.pagePos = :pos ORDER BY  enteredYear , enteredMonth, enteredDay")
    fun getDataByPagePos(pos: Int): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM TaskEntity")
    fun getMaxId(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: TaskEntity)

    @Update
    fun update(data: TaskEntity)

    @Delete
    fun delete(data: TaskEntity)
}
