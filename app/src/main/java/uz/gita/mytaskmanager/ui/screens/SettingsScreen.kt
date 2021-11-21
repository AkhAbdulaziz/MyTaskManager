package uz.gita.mytaskmanager.ui.screens

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.mytaskmanager.R
import uz.gita.mytaskmanager.databinding.ScreenSettingsBinding
import uz.gita.mytaskmanager.ui.viewmodels.SettingsViewModel
import uz.gita.mytaskmanager.utils.scope

@AndroidEntryPoint
class SettingsScreen : Fragment(R.layout.screen_settings) {
    private val binding by viewBinding(ScreenSettingsBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.scope {
        super.onViewCreated(view, savedInstanceState)
        notificationSwitch.colorBorder = ContextCompat.getColor(requireContext(), R.color.baseColor)
        notificationSwitch.colorOn = ContextCompat.getColor(requireContext(), R.color.baseColor)
        notificationSwitch.isOn = viewModel.getNotificationStatus()

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        changeNotification.setOnClickListener {
            if (viewModel.getNotificationStatus()) {
                viewModel.setNotificationStatus(false)
                notificationSwitch.isOn = false
            } else {
                viewModel.setNotificationStatus(true)
                notificationSwitch.isOn = true
            }
        }

        notificationSwitch.setOnToggledListener { toggleableView, isOn ->
            if (isOn) {
                viewModel.setNotificationStatus(true)
                notificationSwitch.isOn = true
            } else {
                viewModel.setNotificationStatus(false)
                notificationSwitch.isOn = false
            }
        }
    }
}