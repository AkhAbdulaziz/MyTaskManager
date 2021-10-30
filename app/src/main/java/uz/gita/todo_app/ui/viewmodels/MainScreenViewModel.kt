package uz.gita.todo_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.todo_app.data.entity.TaskEntity
import uz.gita.todo_app.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

}