package com.awtry.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import java.time.format.DateTimeFormatter

class TodoAdapter(private val list: MutableList<Task>) :
    RecyclerView.Adapter<TodoAdapter.TaskViewHolder>() {

    //funcion para llamar al momento de agrgegar una tarea.
    fun add(task: Task) {
        list.add(task)
        notifyItemInserted(list.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TaskViewHolder {
        //Para rellenar los datos, usamos la parte rellenar
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoAdapter.TaskViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() = list.size

    inner class TaskViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        //Se pasa la info para ser mostrada
        fun bind(data: Task, position: Int) = view.apply {
            val txtTitulo: TextView = findViewById(R.id.txtTitulo)
            val txtFecha: TextView = findViewById(R.id.txtFecha)
            val check_completado: MaterialCheckBox = findViewById(R.id.check_completado)

            txtTitulo.text = data.titulo
            txtFecha.text = data.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm a"))

            check_completado.setOnClickListener {
                list.removeAt(position)
                notifyItemRemoved(position)
            }

            rootView.setOnClickListener { }
        }
    }
}