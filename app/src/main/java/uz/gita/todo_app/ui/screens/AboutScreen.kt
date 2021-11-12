package uz.gita.todo_app.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.todo_app.R
import uz.gita.todo_app.databinding.ScreenAboutBinding
import uz.gita.todo_app.utils.scope

class AboutScreen : Fragment(R.layout.screen_about) {
    private val binding by viewBinding(ScreenAboutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.scope {
        super.onViewCreated(view, savedInstanceState)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}