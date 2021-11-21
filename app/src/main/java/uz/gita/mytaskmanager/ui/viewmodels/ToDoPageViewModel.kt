package uz.gita.mytaskmanager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.mytaskmanager.data.entity.TaskEntity
import uz.gita.mytaskmanager.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class ToDoPageViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    private val _tasksToDoPageLiveData = MutableLiveData<List<TaskEntity>>()
    val tasksToDoPageLiveData: LiveData<List<TaskEntity>> get() = _tasksToDoPageLiveData

    fun insert(data: TaskEntity) {
        repository.insert(data)
    }

    fun update(data: TaskEntity) {
        repository.update(data)
    }

    fun delete(data: TaskEntity) {
        repository.delete(data)
    }

    fun getDataByPagePos() {
        _tasksToDoPageLiveData.value = repository.getDataByPagePos(0)
    }
}