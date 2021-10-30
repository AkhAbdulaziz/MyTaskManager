package uz.gita.todo_app.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.todo_app.R
import uz.gita.todo_app.ui.pages.DoingPage
import uz.gita.todo_app.ui.pages.DonePage
import uz.gita.todo_app.ui.pages.TodoPage

class MainPageAdapter(
    fm : FragmentManager,
    lifecycle : Lifecycle,
    var todoPage: Fragment,
    var doingPage: Fragment,
    var donePage: Fragment
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                todoPage
            }
            1 -> {
                doingPage
            }
            else -> {
                donePage
            }
        }
    }
}
