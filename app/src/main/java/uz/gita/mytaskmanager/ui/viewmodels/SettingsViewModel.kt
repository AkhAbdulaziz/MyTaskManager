package uz.gita.mytaskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.mytaskmanager.domain.AppRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    fun setNotificationStatus(status: Boolean) {
        repository.isNotificationEnabled = status
    }

    fun getNotificationStatus(): Boolean {
        return repository.isNotificationEnabled
    }
}