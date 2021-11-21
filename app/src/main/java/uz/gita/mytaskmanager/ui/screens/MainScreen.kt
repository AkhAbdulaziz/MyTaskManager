package uz.gita.mytaskmanager.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.mytaskmanager.R
import uz.gita.mytaskmanager.app.App
import uz.gita.mytaskmanager.databinding.ScreenMainNavBinding
import uz.gita.mytaskmanager.ui.adapter.MainPageAdapter
import uz.gita.mytaskmanager.ui.pages.DoingPage
import uz.gita.mytaskmanager.ui.pages.DonePage
import uz.gita.mytaskmanager.ui.pages.TodoPage
import uz.gita.mytaskmanager.ui.viewmodels.MainScreenViewModel
import uz.gita.mytaskmanager.utils.scope

@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main_nav),
    NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: MainScreenViewModel by viewModels()
    private val binding by viewBinding(ScreenMainNavBinding::bind)

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
        innerLayout.pager.adapter = adapter
        TabLayoutMediator(innerLayout.tabLayout, innerLayout.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Todo"
                1 -> tab.text = "Doing"
                else -> tab.text = "Done"
            }
        }.attach()

        innerLayout.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    innerLayout.buttonAdd.visibility = View.VISIBLE
                } else
                    innerLayout.buttonAdd.visibility = View.GONE
            }
        })

        innerLayout.menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        innerLayout.buttonAdd.setOnClickListener {
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

        lineAbout.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_aboutScreen)
        }

        lineSettings.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_settingsScreen)
        }

        lineShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val link =
                "Easy manage your tasks!: https://play.google.com/store/apps/details?id=${App.instance.packageName}"
            intent.putExtra(Intent.EXTRA_TEXT, link)
            requireActivity().startActivity(Intent.createChooser(intent, "Share:"))
        }

        lineMoreApps.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse(
                    "https://play.google.com/store/apps/developer?id=GITA+Dasturchilar+Akademiyasi"
                )
            requireActivity().startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _todoPage = null
        _doingPage = null
        _donePage = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = true
}
