package uz.gita.todo_app.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.todo_app.R
import uz.gita.todo_app.databinding.ScreenMainBinding
import uz.gita.todo_app.ui.adapter.MainPageAdapter
import uz.gita.todo_app.ui.pages.DoingPage
import uz.gita.todo_app.ui.pages.DonePage
import uz.gita.todo_app.ui.pages.TodoPage
import uz.gita.todo_app.ui.viewmodels.MainScreenViewModel
import uz.gita.todo_app.utils.scope

@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main) {
    private val viewModel: MainScreenViewModel by viewModels()
    private val binding by viewBinding(ScreenMainBinding::bind)

    private var _todoPage: TodoPage? = TodoPage()
    private val todoPage get() = _todoPage!!

    private var _doingPage: DoingPage? = DoingPage()
    private val doingPage get() = _doingPage!!

    private var _donePage: DonePage? = DonePage()
    private val donePage get() = _donePage!!

    private lateinit var adapter: MainPageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.scope {
        adapter = MainPageAdapter(
            childFragmentManager,
            lifecycle,
            todoPage,
            doingPage,
            donePage
        )
        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Todo"
                1 -> tab.text = "Doing"
                else -> tab.text = "Done"
            }
        }.attach()

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    buttonAdd.visibility = View.VISIBLE
                } else
                    buttonAdd.visibility = View.GONE
            }
        })

        buttonAdd.setOnClickListener {
            findNavController().navigate(MainScreenDirections.actionMainScreenToAddTaskDialog(null))
        }

        todoPage.setUpdateDoingPageListener {
            doingPage.loadData()
        }
        doingPage.setUpdateDonePageListener {
            donePage.loadData()
        }
        doingPage.setUpdateTodoPageListener {
            todoPage.loadData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _todoPage = null
        _doingPage = null
        _donePage = null
    }
}
