package uz.gita.mytaskmanager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.mytaskmanager.data.entity.TaskEntity
import uz.gita.mytaskmanager.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class DonePageViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
    private val _tasksDoneLiveData = MutableLiveData<List<TaskEntity>>()
    val tasksDoneLiveData: LiveData<List<TaskEntity>> get() = _tasksDoneLiveData

    fun update(data: TaskEntity) {
        repository.update(data)
    }

    fun delete(data: TaskEntity) {
        repository.delete(data)
    }

    fun getDataByPagePos() {
        _tasksDoneLiveData.value = repository.getDataByPagePos(2)
    }
}