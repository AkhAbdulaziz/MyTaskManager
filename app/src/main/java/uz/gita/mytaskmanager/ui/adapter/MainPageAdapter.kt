package uz.gita.mytaskmanager.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

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
