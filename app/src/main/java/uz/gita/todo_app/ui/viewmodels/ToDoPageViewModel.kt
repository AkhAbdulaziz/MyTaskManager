package uz.gita.todo_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.todo_app.data.entity.TaskEntity
import uz.gita.todo_app.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class ToDoPageViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    private val _tasksToDoPageLiveData = MutableLiveData<List<TaskEntity>>()
    val tasksToDoPageLiveData: LiveData<List<TaskEntity>> get() = _tasksToDoPageLiveData

    fun insert(data: TaskEntity) {
        repository.insert(data)
        getDataByPagePos()
    }

    fun update(data: TaskEntity) {
        repository.update(data)
        getDataByPagePos()
    }

    fun delete(data: TaskEntity) {
        repository.delete(data)
        getDataByPagePos()
    }

    fun getDataByPagePos() {
        _tasksToDoPageLiveData.value = repository.getDataByPagePos(0)
    }
}