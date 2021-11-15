package com.awtry.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        val NEW_TASK = 200
        val NEW_TASK_LLAVE = "newTask"
    }

    private lateinit var RecicleView_Task: RecyclerView
    private lateinit var btn_nuevaTarea: FloatingActionButton

    private val SAVED_TASK = "task"

    private lateinit var adapter: TodoAdapter

    private var task = mutableListOf(
        Task(0, "Test", "Descripción", LocalDateTime.now()),
        Task(1, "Hola", "La cosa 2", LocalDateTime.of(2021, Month.SEPTEMBER, 7, 12, 50)),
        Task(2, "Awtry", "La cosa 3", LocalDateTime.of(2021, Month.SEPTEMBER, 21, 12, 50)),
        Task(3, "Beatrix", "Miau", LocalDateTime.of(2021, Month.DECEMBER, 21, 12, 50))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Recargar la lista con los nuevos elementos al existir un cambio
        savedInstanceState?.let {
            //Al momento de pasarlo verificamos si esta vacío, si no lo esta, tomará los valores nuevos
            //Si lo esta tomara la lista martillada
            val savedTask = it.getParcelableArrayList<Task>(SAVED_TASK)?.toMutableList() ?: task
            task = savedTask
        }

        initViews()
        setAdapter()
    }

    private fun initViews() {
        RecicleView_Task = findViewById(R.id.RecicleView_Task)
        btn_nuevaTarea = findViewById(R.id.btn_nuevaTarea)

        btn_nuevaTarea.setOnClickListener {
            //adapter.add(Task(0, "Test", "Descripción", LocalDateTime.now()))

            //Le pasamos toda la actividad a la otra vista
            //startActivity(Intent(this, FormActivity::class.java))

            //Se pasa la información, pero con new task ahora espera un resultado al momento de regresar
            startActivityForResult(Intent(this, FormActivity::class.java), NEW_TASK)
        }
    }

    private fun setAdapter() {
        adapter = TodoAdapter(task)

        RecicleView_Task.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        RecicleView_Task.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //Aplicamos los cambios que se hagan, agregando las tareas.
        outState.apply {
            putParcelableArrayList("task", task as ArrayList<Task>)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_TASK){
            //Asegurar que la tarea no viene vacía para poder agregar los datos
            data?.getParcelableExtra<Task>(NEW_TASK_LLAVE)?.let {
                adapter.add(it)
            }

        }
    }

}