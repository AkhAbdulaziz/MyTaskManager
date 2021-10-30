package uz.gita.todo_app.ui.pages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.todo_app.R
import uz.gita.todo_app.data.entity.TaskEntity
import uz.gita.todo_app.ui.adapter.TaskAdapter
import uz.gita.todo_app.ui.dialog.EventDialog
import uz.gita.todo_app.ui.screens.AddTaskScreen
import uz.gita.todo_app.ui.screens.MainScreenDirections
import uz.gita.todo_app.ui.viewmodels.DonePageViewModel

@AndroidEntryPoint
class DonePage : Fragment(R.layout.page_done) {
    private val data = ArrayList<TaskEntity>()
    private val viewModel: DonePageViewModel by viewModels()
    private val adapter by lazy { TaskAdapter(data) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv: RecyclerView = view.findViewById(R.id.doneList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        loadData()

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
                findNavController().navigate(MainScreenDirections.actionMainScreenToAddTaskDialog(data[pos]))
            }
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "event")
        }
        viewModel.tasksDoneLiveData.observe(viewLifecycleOwner, donePageObserver)
    }

    fun loadData() {
        view?.let {
            data.clear()
            viewModel.getDataByPagePos()
        }
    }

    private val donePageObserver = Observer<List<TaskEntity>> {
        data.addAll(it)
        adapter.notifyDataSetChanged()
    }
}