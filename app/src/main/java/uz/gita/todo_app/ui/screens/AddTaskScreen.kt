package uz.gita.todo_app.ui.screens

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.todo_app.R
import uz.gita.todo_app.app.App
import uz.gita.todo_app.data.ToDoWorker
import uz.gita.todo_app.data.entity.TaskEntity
import uz.gita.todo_app.databinding.ScreenAddTaskBinding
import uz.gita.todo_app.ui.viewmodels.AddTaskViewModel
import uz.gita.todo_app.utils.*
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AddTaskScreen : Fragment(R.layout.screen_add_task) {
    private var listener: ((TaskEntity) -> Unit)? = null
    fun setEditDialogListener(f: (TaskEntity) -> Unit) {
        listener = f
    }

    private val binding by viewBinding(ScreenAddTaskBinding::bind)
    private val viewModel: AddTaskViewModel by viewModels()
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var notificationId: String
    private var isEditing = false
    private var dataId = 0
    private var pagePos = 0
    private val args: AddTaskScreenArgs by navArgs()
    private var enteredDay = 0
    private var enteredMonth = 0
    private var enteredYear = 0
    private var isTimeSelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.scope {

        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        isEditing = false

        if (args.data != null) {
            isEditing = true
            val data = args.data
            addNoteTitle.setText(data!!.task)
            addNoteCalendar.setText(data.day)
            addNoteTime.setText(data.time)
            dataId = data.id
            pagePos = data.pagePos
            notificationId = data.notificationId
            enteredYear = data.enteredYear
            enteredMonth = data.enteredMonth
            enteredDay = data.enteredDay
        }

        /* addNoteTitle.background.setColorFilter(
             ContextCompat.getColor(
                 requireContext(),
                 R.color.white
             ), PorterDuff.Mode.SRC_IN
         )*/

        addNoteTitle.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboardFrom(requireContext(), view)
            }
        }

        calendarImg.setOnClickListener {
            hideKeyboardFrom(requireContext(), addNoteTitle)
            addNoteTitle.clearFocus()
            showDatePicker()
        }

        addNoteCalendar.setOnClickListener {
            hideKeyboardFrom(requireContext(), addNoteTitle)
            addNoteTitle.clearFocus()
            showDatePicker()
        }

        timeImg.setOnClickListener {
            hideKeyboardFrom(requireContext(), addNoteTitle)
            addNoteTitle.clearFocus()
            showTimePicker()
        }

        addNoteTime.setOnClickListener {
            hideKeyboardFrom(requireContext(), addNoteTitle)
            addNoteTitle.clearFocus()
            showTimePicker()
        }

        addNoteBtn.setOnClickListener {

            if (isEditing) {
                listener?.invoke(
                    TaskEntity(
                        dataId,
                        addNoteTitle.text.toString(),
                        addNoteCalendar.text.toString(),
                        addNoteTime.text.toString(),
                        pagePos,
                        notificationId,
                        0,
                        enteredYear,
                        enteredMonth,
                        enteredDay
                    )
                )
                updateWorkRequest(dataId, pagePos, notificationId)
            } else {
                createWorkRequest()
            }
        }

        cancelNoteBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showDatePicker() = binding.scope {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        var dateText = ""
        val datePickerDialog = DatePickerDialog(requireContext(), { datepicker, year, month, day ->
            val m = month + 1
            dateText = "$day/$m/$year"
            enteredDay = day
            enteredMonth = m
            enteredYear = year


            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day
            addNoteCalendar.setText("$dateText")
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.baseColor))

    }

    private fun showTimePicker() = binding.scope {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            var hour = ""
            var minute = ""
            hour = if (picker.hour < 10) {
                "0${picker.hour}"
            } else
                "${picker.hour}"
            minute =
                if (picker.minute < 10) {
                    "0${picker.minute}"
                } else {
                    "${picker.minute}"
                }

            addNoteTime.setText("$hour:$minute")
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            isTimeSelected = true
        }

        picker.show(childFragmentManager, "pickerTag")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("T", "Demo", NotificationManager.IMPORTANCE_DEFAULT)
            val manager =
                App.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    private fun createWorkRequest() = binding.scope {
        val calendarHelp = Calendar.getInstance()
        val day = calendarHelp.get(Calendar.DAY_OF_MONTH)
        val month = calendarHelp.get(Calendar.MONTH) + 1
        val year = calendarHelp.get(Calendar.YEAR)

        createNotificationChannel()
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val isDayNotPast =
            enteredYear > year || (enteredYear == year && enteredMonth > month) || (enteredYear == year && enteredMonth == month && enteredDay > day) || (enteredYear == year && enteredMonth == month && enteredDay == day && calendar.timeInMillis > System.currentTimeMillis())

        if (isDayNotPast && addNoteTitle.text != null && addNoteTitle.text!!.isNotEmpty() && isTimeSelected) {
            val data = Data.Builder()
            data.putInt("id", viewModel.getMaxId())
            data.putString("title", addNoteTitle.text.toString())

            val request = OneTimeWorkRequest.Builder(ToDoWorker::class.java)
                .setConstraints(constraints)
                .setInitialDelay(
                    Math.abs(calendar.timeInMillis - System.currentTimeMillis()),
                    TimeUnit.MILLISECONDS
                )
                .setInputData(data.build())
                .build()
            notificationId = request.id.toString()
            WorkManager.getInstance(requireContext()).enqueue(request)
            viewModel.insert(
                TaskEntity(
                    0,
                    binding.addNoteTitle.text.toString(),
                    binding.addNoteCalendar.text.toString(),
                    binding.addNoteTime.text.toString(),
                    0,
                    notificationId,
                    calendar.timeInMillis,
                    enteredYear,
                    enteredMonth,
                    enteredDay
                )
            )
            findNavController().popBackStack()
        } else if (addNoteTitle.text!!.isEmpty()) {
            showToast("Task is empty, please type your task!")
        } else if (!isTimeSelected) {
            showToast("Time is not selected, please select it!")
        } else {
            showToast("You cannot add task for past time!")
        }
    }

    private fun updateWorkRequest(id: Int, pagePos: Int, notificationId: String) = binding.scope {
        cancelRequest(notificationId)

        val calendarHelp = Calendar.getInstance()
        val day = calendarHelp.get(Calendar.DAY_OF_MONTH)
        val month = calendarHelp.get(Calendar.MONTH) + 1
        val year = calendarHelp.get(Calendar.YEAR)

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val isDayNotPast =
            enteredYear > year || (enteredYear == year && enteredMonth > month) || (enteredYear == year && enteredMonth == month && enteredDay > day) || (enteredYear == year && enteredMonth == month && enteredDay == day && calendar.timeInMillis > System.currentTimeMillis())

        if (isDayNotPast && addNoteTitle.text != null && addNoteTitle.text!!.isNotEmpty()) {

            val data = Data.Builder()

            data.putInt("id", viewModel.getMaxId())
            data.putString("title", addNoteTitle.text.toString())

            val request = OneTimeWorkRequest.Builder(ToDoWorker::class.java)
                .setConstraints(constraints)
                .setInitialDelay(
                    calendar.timeInMillis - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .setInputData(data.build())
                .build()
            this@AddTaskScreen.notificationId = request.id.toString()
            WorkManager.getInstance(requireContext()).enqueue(request)
            viewModel.update(
                TaskEntity(
                    id,
                    binding.addNoteTitle.text.toString(),
                    binding.addNoteCalendar.text.toString(),
                    binding.addNoteTime.text.toString(),
                    pagePos,
                    this@AddTaskScreen.notificationId,
                    calendar.timeInMillis,
                    enteredYear,
                    enteredMonth,
                    enteredDay
                )
            )
            findNavController().popBackStack()
        } else if (addNoteTitle.text!!.isEmpty()) {
            showToast("Task is empty, please type your task!")
        } else if (!isTimeSelected) {
            showToast("Time is not selected, please select it!")
        } else {
            showToast("You cannot add task for past time!")
        }
    }
}
