package com.example.todolist.recyclerrequirements

import Task
import android.app.AlertDialog
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(private val myList : ArrayList<Task>, private val listener: OnItemClickListener)
    : RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    interface OnItemClickListener{
            fun onItemClick(task: Task)
    }

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.design_layout,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = myList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    //function for responding with the database
    fun setTasks(tasks: List<Task>) {
        myList.clear()
        myList.addAll(tasks)
        notifyDataSetChanged()
    }


   inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener
    {
            private val toDoName: TextView = itemView.findViewById(R.id.oneD)
            private val toDoDescription: TextView = itemView.findViewById(R.id.twoD)
            private val dateTimeBtn : TextView = itemView.findViewById(R.id.threeD)
            private val removeBtn: ImageButton = itemView.findViewById(R.id.removeList)
            private val redoBtn: ImageButton = itemView.findViewById(R.id.redoList)

            // listener
            init {
                itemView.setOnClickListener(this)


                removeBtn.setOnClickListener{

                    val position = adapterPosition //GET POSITION
                    if(position != RecyclerView.NO_POSITION)
                    {
                        val task = myList[position]
                        showConfirmationDialog(task)
//                        isCompleted = !isCompleted
//                        if(isCompleted)
//                        {

                            // Apply strike-through text style
//                            toDoName.paintFlags = toDoName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                            toDoDescription.paintFlags = toDoDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                            dateTimeBtn.paintFlags = dateTimeBtn.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                            databaseReUpdate(task,false)

//                        }
                    }
                }// end of remove button method

                redoBtn.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION)
                    {
                        val task = myList[position]
                        showConfirmation(task)
                    }
                }


            }

        // Show confirmation dialog for deletion
        private fun showConfirmationDialog(task: Task) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("STRIKE OUT THE TASK")
                .setMessage("Mark the task as Done?")
                .setPositiveButton("Yes") { dialog, _ ->
                    applyStrikeThrough()
                    databaseReUpdate(task, false)
                    removeBtn.visibility = View.GONE
                    redoBtn.visibility = View.VISIBLE
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Show confirmation dialog for deletion
        private fun showConfirmation(task: Task) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("REDO THE TASK")
                .setMessage("Mark the task as Un-Done?")
                .setPositiveButton("Yes") { dialog, _ ->
                    removeStrikeThrough()
                    databaseReUpdate(task, true)
                    removeBtn.visibility = View.VISIBLE
                    redoBtn.visibility = View.GONE
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        //==========================DATABASE ============================

        private fun databaseReUpdate(task: Task, isCompleted: Boolean) {
            val taskId = task.id

            val db = FirebaseFirestore.getInstance()
            if (taskId != null) {
                db.collection("tasks").document(taskId)
                    .update("completed", isCompleted)
                    .addOnSuccessListener {
                        // Update successful
                    }
                    .addOnFailureListener { e ->
                        // Error occurred during update
                        // Handle the failure case
                    }
            }
        }

        // Apply strike-through effect to the text
        private fun applyStrikeThrough()
        {
            toDoName.paintFlags = toDoName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            toDoDescription.paintFlags = toDoDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            dateTimeBtn.paintFlags = dateTimeBtn.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        private fun removeStrikeThrough()
        {
            // Remove strike-through effect
            toDoName.paintFlags = toDoName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            toDoDescription.paintFlags = toDoDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            dateTimeBtn.paintFlags = dateTimeBtn.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        //=====================BIND FUNCTION ============

        fun bind(task: Task)
        {
            toDoName.text = task.name
            toDoDescription.text = task.description
            dateTimeBtn.text = task.finalDT

            // if not true
            if (!task.completed) {
                applyStrikeThrough()
                removeBtn.visibility = View.GONE
                redoBtn.visibility = View.VISIBLE
            }else{
                removeStrikeThrough()
                removeBtn.visibility = View.VISIBLE
                redoBtn.visibility = View.GONE
            }
        }

            override fun onClick(v:View)
            {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = myList[position]
                    listener.onItemClick(task)
                }
            }

    }
}

