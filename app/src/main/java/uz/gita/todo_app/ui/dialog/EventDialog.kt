package uz.gita.todo_app.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.todo_app.R
import uz.gita.todo_app.data.entity.TaskEntity

@AndroidEntryPoint
class EventDialog : BottomSheetDialogFragment() {
    private var editListener: (() -> Unit)? = null
    fun setEditListener(f: () -> Unit) {
        editListener = f
    }

    private var deleteListener: (() -> Unit)? = null
    fun setDeleteListener(f: () -> Unit) {
        deleteListener = f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            val data = it.getSerializable("data") as TaskEntity
            if (data.pagePos != 0) {
                val editLayout: LinearLayout = view.findViewById(R.id.edit)
                editLayout.visibility = View.GONE
            }
        }
        view.findViewById<View>(R.id.edit).setOnClickListener {
            editListener?.invoke()
            dismiss()
        }
        view.findViewById<View>(R.id.delete).setOnClickListener {
            deleteListener?.invoke()
            dismiss()
        }
    }
}