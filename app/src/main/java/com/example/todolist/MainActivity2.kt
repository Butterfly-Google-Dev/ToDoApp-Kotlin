package com.example.todolist

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity2 : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    private lateinit var updateName: ImageView
    private lateinit var updateDate: ImageView
    private lateinit var updateDescription: ImageView

    private lateinit var taskNameTextView: TextView
    private lateinit var taskDescriptionTextView: TextView
    private lateinit var finalDTTextView: TextView


    var day = 0
    var month = 0
    var year = 0

    var currentDay = 0
    var currentMonth = 0
    var currentYear = 0

    private var finalDT =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Set the values in the UI
        taskNameTextView = findViewById(R.id.firstText)
        taskDescriptionTextView = findViewById(R.id.secondText)
        finalDTTextView = findViewById(R.id.thirdText)

        taskNameTextView.text = intent.getStringExtra("taskName")
        taskDescriptionTextView.text = intent.getStringExtra("taskDescription")
        finalDTTextView.text = intent.getStringExtra("finalDT")

        val completed = intent.getStringExtra("completed")


        Log.d(completed, "Debug message")


        if (completed == "false") {
            taskNameTextView.paintFlags = taskNameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            taskDescriptionTextView.paintFlags = taskDescriptionTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            finalDTTextView.paintFlags = finalDTTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else
        {
            taskNameTextView.text = intent.getStringExtra("taskName")
            taskDescriptionTextView.text = intent.getStringExtra("taskDescription")
            finalDTTextView.text = intent.getStringExtra("finalDT")
        }

        edtName()
        edtDescription()
        pickDate()


    }
    //==========================TASK NAME==========================================================
    private fun edtName()
    {
        updateName = findViewById(R.id.nameUpdate)

        updateName.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Edit Task Name")
            val input = EditText(this)

            //showing in alert box the past messages
            input.setText(taskNameTextView.text)
            alertDialog.setView(input)

            alertDialog.setPositiveButton("Save") { _, _ ->

                val newName = input.text.toString().trim()
                val name: String = "name"
                if (newName.isNotEmpty()) {

                    taskNameTextView.text = newName

                    updateData(newName,name)

                } else {
                    Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            alertDialog.show()
        }
    }

    //==========================TASK DESCRIPTION=====================================================

    private fun edtDescription()
    {
        updateDescription = findViewById(R.id.descriptionUpdate)

        updateDescription.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Edit Task Description")
            val input = EditText(this)
            //showing in alert box the past messages
            input.setText(taskDescriptionTextView.text)
            alertDialog.setView(input)

            alertDialog.setPositiveButton("Save") { _, _ ->

                val newName = input.text.toString().trim()
                val name: String = "description"
                if (newName.isNotEmpty()) {

                    taskDescriptionTextView.text = newName

                    updateData(newName,name)

                } else {
                    Toast.makeText(this, "Task Description cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            alertDialog.show()
        }
    }

    //==========================TASK DATABASE==========================================================


    private fun updateData(newValue: String, fieldName: String) {

        val taskId = intent.getStringExtra("taskId")

        val db = FirebaseFirestore.getInstance()
        if (taskId != null) {

            db.collection("tasks").document(taskId)
                .update(fieldName, newValue)
                .addOnSuccessListener {

                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update task name", Toast.LENGTH_SHORT).show()
                }

        }
    }

    //===============================TASK CALENDER=====================================================

    //function to set the format of date and time
    private fun getTimeCalender()
    {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)

    }

    // Function to pick Date
    private fun pickDate()
    {
        updateDate = findViewById(R.id.dateUpdate)
        updateDate.setOnClickListener {

            getTimeCalender()

            val datePickerDialog = DatePickerDialog(
                this,
                { datePicker, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)

                    val currentCalendar = Calendar.getInstance()
                    currentCalendar.set(currentYear, currentMonth, currentDay)

                    if (selectedCalendar >= currentCalendar) {
                        currentDay = dayOfMonth
                        currentMonth = month
                        currentYear = year

                        finalDT = "$currentDay-$currentMonth-$currentYear"
                        updateData(finalDT,"finalDT")
                        finalDTTextView.text = finalDT

                    } else {
                        Toast.makeText(this, "Please select a date from today onwards.", Toast.LENGTH_SHORT).show()
                    }
                }, currentYear, currentMonth, currentDay)

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    // methods implemented from date picker and time picker
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayofMonth: Int) {

        currentDay = dayofMonth
        currentMonth = month
        currentYear = year
    }

    // OVERRIDE BACK PRESS FUNCTION
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

}