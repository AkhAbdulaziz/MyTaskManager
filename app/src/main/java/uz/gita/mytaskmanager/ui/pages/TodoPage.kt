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
import uz.gita.mytaskmanager.databinding.PageToDoBinding
import uz.gita.mytaskmanager.ui.adapter.TaskAdapter
import uz.gita.mytaskmanager.ui.dialog.EventDialog
import uz.gita.mytaskmanager.ui.screens.AddTaskScreen
import uz.gita.mytaskmanager.ui.screens.MainScreenDirections
import uz.gita.mytaskmanager.ui.viewmodels.ToDoPageViewModel
import uz.gita.mytaskmanager.utils.cancelRequest

@AndroidEntryPoint
class TodoPage : Fragment(R.layout.page_to_do) {
    private val binding by viewBinding(PageToDoBinding::bind)
    private val data = ArrayList<TaskEntity>()
    private val adapter by lazy { TaskAdapter(data) }
    private val viewModel: ToDoPageViewModel by viewModels()
    private var updateDoingPageListener: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv: RecyclerView = view.findViewById(R.id.todoList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        loadData()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    val taskEntity = data[viewHolder.absoluteAdapterPosition]
                    cancelRequest(taskEntity.notificationId)
                    taskEntity.pagePos = 1
                    data.removeAt(viewHolder.absoluteAdapterPosition)
                    viewModel.update(taskEntity)
                    adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                    updateDoingPageListener?.invoke()
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
        viewModel.tasksToDoPageLiveData.observe(viewLifecycleOwner, toDoPageObserver)
    }

    fun loadData() {
        view?.let {
            data.clear()
            viewModel.getDataByPagePos()
        }
    }

    private val toDoPageObserver = Observer<List<TaskEntity>> {
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

    fun setUpdateDoingPageListener(f: () -> Unit) {
        updateDoingPageListener = f
    }
}

