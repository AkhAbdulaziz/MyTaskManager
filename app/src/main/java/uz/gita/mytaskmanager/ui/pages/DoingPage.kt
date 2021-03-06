package uz.gita.mytaskmanager.ui.pages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.mytaskmanager.R
import uz.gita.mytaskmanager.data.entity.TaskEntity
import uz.gita.mytaskmanager.databinding.PageDoingBinding
import uz.gita.mytaskmanager.ui.adapter.TaskAdapter
import uz.gita.mytaskmanager.ui.dialog.EventDialog
import uz.gita.mytaskmanager.ui.screens.AddTaskScreen
import uz.gita.mytaskmanager.ui.screens.MainScreenDirections
import uz.gita.mytaskmanager.ui.viewmodels.DoingPageViewModel
import uz.gita.mytaskmanager.utils.cancelRequest
import uz.gita.mytaskmanager.utils.recreateWorkRequest

@AndroidEntryPoint
class DoingPage : Fragment(R.layout.page_doing) {
    private val binding by viewBinding(PageDoingBinding::bind)
    private val data = ArrayList<TaskEntity>()
    private val viewModel: DoingPageViewModel by viewModels()
    private val adapter by lazy { TaskAdapter(data) }

    private var updateTodoPageListener: (() -> Unit)? = null
    private var updateDonePageListener: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv: RecyclerView = view.findViewById(R.id.doingList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        loadData()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskEntity = data[viewHolder.absoluteAdapterPosition]
                if (direction == ItemTouchHelper.RIGHT) {
                    taskEntity.pagePos = 2
                    data.removeAt(viewHolder.absoluteAdapterPosition)
                    viewModel.update(taskEntity)
                    adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                    updateDonePageListener?.invoke()
                } else {
                    val condition = recreateWorkRequest(taskEntity)
                    if (condition) {
                        taskEntity.pagePos = 0
                        viewModel.update(taskEntity)
                        data.removeAt(viewHolder.absoluteAdapterPosition)
                        adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                        updateTodoPageListener?.invoke()
                    } else {
                        taskEntity.pagePos = 1
                        viewModel.update(taskEntity)
                        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        updateTodoPageListener?.invoke()
                    }
                }
                checkAnimationView()
            }
        })
        itemTouchHelper.attachToRecyclerView(rv)

        adapter.setListener { pos ->
            val dialog = EventDialog()
            val bundle = Bundle()
            bundle.putSerializable("data", data[pos])

            dialog.setDeleteListener {
                viewModel.delete(data[pos])
                cancelRequest(data[pos].notificationId)
                data.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                checkAnimationView()
            }

            dialog.setEditListener {
                val editDialog = AddTaskScreen()
                editDialog.setEditDialogListener {
                    data[pos] = it
                    viewModel.update(it)
                    adapter.notifyItemChanged(pos)
                }
                findNavController().navigate(
                    MainScreenDirections.actionMainScreenToAddTaskDialog(
                        data[pos]
                    )
                )
            }
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "event")
        }
        viewModel.tasksDoingLiveData.observe(viewLifecycleOwner, doingPageObserver)
    }

    fun loadData() {
        view?.let {
            data.clear()
            viewModel.getDataByPagePos()
        }
    }

    private val doingPageObserver = Observer<List<TaskEntity>> {
        data.clear()
        data.addAll(it)
        adapter.notifyDataSetChanged()
        checkAnimationView()
    }

    private fun checkAnimationView() {
        if (data.isEmpty()) {
            binding.animationView.playAnimation()
            binding.animationView.visibility = View.VISIBLE
        } else {
            binding.animationView.visibility = View.GONE
            binding.animationView.pauseAnimation()
        }
    }

    fun setUpdateTodoPageListener(f: () -> Unit) {
        updateTodoPageListener = f
    }

    fun setUpdateDonePageListener(f: () -> Unit) {
        updateDonePageListener = f
    }
}