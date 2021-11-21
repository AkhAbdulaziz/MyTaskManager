package uz.gita.mytaskmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val task: String,
    val day: String,
    val time: String,
    var pagePos: Int = 0,
    var notificationId: String = "",
    var timeInMilliSeconds: Long = 0,
    var enteredYear: Int,
    var enteredMonth: Int,
    var enteredDay: Int
) : Serializable