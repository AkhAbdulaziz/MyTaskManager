package uz.gita.mytaskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.mytaskmanager.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

}