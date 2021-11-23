package com.awtry.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import java.time.format.DateTimeFormatter

class TodoAdapter(
    private val list: MutableList<Task>,
    var onClickDoneTask: (task: Task, position: Int) -> Unit,
    var onClickDetailTask: (task: Task) -> Unit
) :
    RecyclerView.Adapter<TodoAdapter.TaskViewHolder>() {

    //funcion para llamar al momento de agrgegar una tarea.
    fun add(task: Task) {
        list.add(task)
        notifyItemInserted(list.size - 1)
    }

    fun update(task: Task) {
        val index = list.indexOfFirst { it.id == task.id }
        list[index] = task
        notifyItemChanged(index)
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, list.size)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TaskViewHolder {
        //Para rellenar los datos, usamos la parte rellenar
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoAdapter.TaskViewHolder, position: Int) {
        //holder.bind(list[position])
        (holder as TaskViewHolder).bind(list[position], position)
    }

    override fun getItemCount() = list.size

    inner class TaskViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        //Se pasa la info para ser mostrada
        fun bind(data: Task, position: Int) = view.apply {
            val txtTitulo: TextView = findViewById(R.id.txtTitulo)
            val txtFecha: TextView = findViewById(R.id.txtFecha)
            val check_completado: MaterialCheckBox = findViewById(R.id.check_completado)

            txtTitulo.text = data.titulo
            txtFecha.text = data.fecha?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))

            check_completado.isChecked = false

            check_completado.setOnClickListener {
                //list.removeAt(adapterPosition)
                //notifyItemRemoved(position)
                //notifyItemRangeChanged(position,list.size)

                //layoutPosition

                //notifyItemRemoved(adapterPosition)
                onClickDoneTask(data, adapterPosition)
            }

            rootView.setOnClickListener {
                onClickDetailTask(data)
            }
        }
    }
}