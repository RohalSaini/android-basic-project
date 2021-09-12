package com.example.demo.Adapter

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.DbHelper
import com.example.demo.R
import com.example.demo.modal.Todo
import com.google.android.material.snackbar.Snackbar

class RecylerViewAdpater(var context: Context, var list:MutableList<Todo>) : RecyclerView.Adapter<com.example.demo.Adapter.RecylerViewAdpater.ViewHolder>() {

    override fun getItemId(positon: Int): Long {
        return positon.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.description.text = list[position].description
        holder.job.text = list[position].job
        holder.view.setOnClickListener {
            updateItem(it,position,list[position])
        }
        holder.view.setOnLongClickListener {
            removeItem(view = it,position=position,todo= list[position])
            return@setOnLongClickListener true
        }
    }

    private fun updateItem(it: View?, position: Int, todo: Todo) {
        showDialogBox(todo,it,position)
    }

    private fun showDialogBox(todo: Todo, view: View?, position: Int) {
        var dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_update_todo)

        //setting description value
        var description = dialog.findViewById<EditText>(R.id.editext_description)
        description.text = Editable.Factory.getInstance().newEditable(todo.description)

        // setting name for todo
        var name = dialog.findViewById<EditText>(R.id.editext_job_name)
        name.text = Editable.Factory.getInstance().newEditable(todo.job)

        var update = dialog.findViewById<Button>(R.id.btn_add)

        update.setOnClickListener {
            if(checkValidate(description,name,it)) {
                if(name.text.trim()!=todo.job) {
                    todo.job = name.text.trim().toString()
                }
                if(description.text.trim()!= todo.description) {
                    todo.description = description.text.toString()
                }
                var db = DbHelper(context)
                db.updateTodo(todo = todo)
                list[position]= todo
                notifyItemChanged(position)
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    private fun checkValidate(description: EditText, name: EditText,view: View):Boolean {
        if(name.text.trim().isEmpty()) {
            Snackbar
                .make(view,"Please fill job , it should not be blank or empty",Snackbar.LENGTH_LONG).show()
            return false
        }
        if(description.text.trim().isEmpty()) {
            Snackbar
                .make(view,"Please fill description, it should not be blank or empty",Snackbar.LENGTH_LONG).show()
            return false
        }
            return true
        }

    private fun removeItem(view: View, position: Int, todo: Todo) {
        var db = DbHelper(context)
        var status = db.deleteTodo(view,todo)
        if(status) {
            list.remove(todo)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view:View = view
        var job: TextView = view.findViewById(R.id.adpater_username)
        var description: TextView = view.findViewById(R.id.adpater_from)
    }
}