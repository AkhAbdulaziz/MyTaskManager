package uz.gita.mytaskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.mytaskmanager.data.entity.TaskEntity
import uz.gita.mytaskmanager.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(private val repository: AppRepository) : ViewModel(){

    fun insert(data : TaskEntity){
        repository.insert(data)
    }
    fun update(data : TaskEntity){
        repository.update(data)
    }

    fun getMaxId() : Int = repository.getMaxId()
}
