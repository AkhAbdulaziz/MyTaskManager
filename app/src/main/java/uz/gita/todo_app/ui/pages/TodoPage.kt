package uz.gita.todo_app.ui.pages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.todo_app.R
import uz.gita.todo_app.data.entity.TaskEntity
import uz.gita.todo_app.ui.adapter.TaskAdapter
import uz.gita.todo_app.ui.dialog.EventDialog
import uz.gita.todo_app.ui.screens.AddTaskScreen
import uz.gita.todo_app.ui.screens.MainScreenDirections
import uz.gita.todo_app.ui.viewmodels.ToDoPageViewModel
import uz.gita.todo_app.utils.cancelRequest

@AndroidEntryPoint
class TodoPage : Fragment(R.layout.page_to_do) {
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
            }
        })
        itemTouchHelper.attachToRecyclerView(rv)

        adapter.setListener { pos ->
            val dialog = EventDialog()
            val bundle = Bundle()
            bundle.putSerializable("data", data[pos])
            dialog.setDeleteListener {
                viewModel.delete(data[pos])
                data.removeAt(pos)
                adapter.notifyItemRemoved(pos)
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
        data.clear()
        viewModel.getDataByPagePos()
    }

    private val toDoPageObserver = Observer<List<TaskEntity>> {
        data.clear()
        data.addAll(it)
        adapter.notifyDataSetChanged()
    }

    fun setUpdateDoingPageListener(f: () -> Unit) {
        updateDoingPageListener = f
    }
}

