package uz.gita.todo_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.gita.todo_app.R
import uz.gita.todo_app.data.entity.TaskEntity

class TaskAdapter(private val list: ArrayList<TaskEntity>) :
    RecyclerView.Adapter<TaskAdapter.VH>() {
    private var listener: ((Int) -> Unit)? = null
    fun setListener(f: (Int) -> Unit) {
        listener = f
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textDateAndTime: TextView = itemView.findViewById(R.id.textDateAndTime)

        init {
            itemView.setOnClickListener {
                listener?.invoke(absoluteAdapterPosition)
                return@setOnClickListener
            }
        }

        fun bind() {
            val data = list[absoluteAdapterPosition]
            when (data.pagePos) {
                0 -> {
                    itemView.setBackgroundResource(R.drawable.item_todo_bg)
                    textTitle.text = data.task
                    textDateAndTime.text = "${data.day}, ${data.time} "
                }
                1 -> {
                    itemView.setBackgroundResource(R.drawable.item_doing_bg)
                    textTitle.text = data.task
                    textDateAndTime.text = "You started this task"
                }
                2 -> {
                    itemView.setBackgroundResource(R.drawable.item_done_bg)
                    textTitle.text = data.task
                    textDateAndTime.text = "Done"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind()

    override fun getItemCount(): Int = list.size
}