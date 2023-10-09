package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Month
import java.util.Calendar

class ToAddList : AppCompatActivity(),DatePickerDialog.OnDateSetListener{

    var day = 0
    var month = 0
    var year = 0

    var currentDay = 0
    var currentMonth = 0
    var currentYear = 0

    private var finalDT =""

    private lateinit var dateTimeBtn: Button
    private lateinit var toDoName: EditText
    private lateinit var toDoDescription: EditText
    private lateinit var doneButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_add_list)


        FirebaseApp.initializeApp(this)
        toDoList()
        pickDate()

    }

    //TO DO List Function
    private fun toDoList()
    {
        doneButton = findViewById(R.id.doneBtn)
        toDoName = findViewById(R.id.toDoName)
        toDoDescription = findViewById(R.id.toDoDescription)
        dateTimeBtn  = findViewById(R.id.dateTimeBtn)

        doneButton.setOnClickListener {
            val taskName = toDoName.text.toString().trim()
            val taskDescription = toDoDescription.text.toString().trim()

            if (taskName.isNotEmpty() && taskDescription.isNotEmpty() && finalDT.isNotEmpty()) {

                databaseCloud(taskName, taskDescription, finalDT)

            } else {
                Toast.makeText(
                    this,
                    "Please enter Task name, description and set a date and time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }



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
        dateTimeBtn.setOnClickListener {

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
                        dateTimeBtn.text = finalDT
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

        getTimeCalender() //calling the function to update the time on the calender
        finalDT = "$currentDay-$currentMonth-$currentYear"
        dateTimeBtn.text = finalDT

    }





    // Database Firestore Cloud
    private fun databaseCloud(taskName: String, taskDescription: String, finalDt: String)
    {

        val db = FirebaseFirestore.getInstance()

        // Save the task to Firestore
        val task = hashMapOf(
            "name" to taskName,
            "description" to taskDescription,
            "timestamp" to FieldValue.serverTimestamp(),
            "finalDT" to finalDT,
            "completed" to true

        )

        db.collection("tasks")
            .add(task)
            .addOnSuccessListener {

                startActivity(Intent(this,MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { e ->

                Toast.makeText(this,"Cannot connect to server",Toast.LENGTH_SHORT).show()


            }

    }


}