package com.example.todolist

import Task
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.recyclerrequirements.TaskAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(),TaskAdapter.OnItemClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var text: TextView
    private val handler = Handler()
 

    companion object {
        private const val REFRESH_INTERVAL = 2000L // Refresh interval in milliseconds (2 seconds)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView )
        text = findViewById(R.id.textOne)

        adapter = TaskAdapter(ArrayList(),this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        firestore = FirebaseFirestore.getInstance()
        databaseCloud()

        val fab: FloatingActionButton = findViewById(R.id.addList)
        fab.setOnClickListener{

            val intent = Intent(this,ToAddList::class.java)
            startActivity(intent)
        }
    }

    private fun databaseCloud()
    {

        firestore.collection("tasks").orderBy("timestamp", Query.Direction.DESCENDING)
            .get()

            .addOnSuccessListener { result ->

                val tasks = ArrayList<Task>()
                for(document in result)
                {
                    val taskName = document.getString("name") ?: ""
                    val taskDescription = document.getString("description") ?: ""
                    val finalDT = document.getString("finalDT") ?: ""
                    val completed = document.getBoolean("completed") ?: ""
                    val task = Task(document.id,taskName, taskDescription, finalDT,
                        completed as Boolean
                    )
                    tasks.add(task)
                }

                adapter.setTasks(tasks)
                text.visibility = if(tasks.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"Cannot fetch data",Toast.LENGTH_SHORT).show()

            }
    }

    override fun onItemClick(task: Task) {

        val intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("taskName", task.name)
        intent.putExtra("taskDescription", task.description)
        intent.putExtra("finalDT", task.finalDT)
        intent.putExtra("taskId",task.id)
        intent.putExtra("completed",task.completed.toString())
        startActivity(intent)
    }

    // OVERRIDE BACK PRESS TO EXIT
    override fun onBackPressed() {
        finishAffinity()
    }


    override fun onResume() {
        super.onResume()

        val refreshRunnable = object : Runnable {
            override fun run() {
                databaseCloud() // Perform the refresh operation
                handler.postDelayed(this, REFRESH_INTERVAL) // Schedule the next refresh
            }
        }

        // Start the initial refresh
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    override fun onPause() {
        super.onPause()

        // Stop the refresh when the activity is paused
        handler.removeCallbacksAndMessages(null)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_min,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.deleteMin ->{
                deleteData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteData()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete all the tasks?")
            .setPositiveButton("Yes") { dialog, _ ->

                firestore.collection("tasks").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                        }
                    }
                    .addOnFailureListener { exception ->

                        Toast.makeText(this, "Failed to delete tasks", Toast.LENGTH_SHORT).show()
                    }

                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}