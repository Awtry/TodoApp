package com.awtry.todoapp

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

    private lateinit var RecicleView_Task: RecyclerView
    private lateinit var btn_nuevaTarea: FloatingActionButton

    private lateinit var adapter: TodoAdapter

    private val task = mutableListOf(
        Task(0, "Test", "Descripción", LocalDateTime.now()),
        Task(1, "Hola", "La cosa 2", LocalDateTime.of(2021,Month.SEPTEMBER, 7, 12,50)),
        Task(2, "Awtry", "La cosa 3", LocalDateTime.of(2021,Month.SEPTEMBER, 21, 12,50)),
        Task(3, "Beatrix", "Miau", LocalDateTime.of(2021,Month.DECEMBER, 21, 12,50))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setAdapter()
    }

    private fun initViews() {
        RecicleView_Task = findViewById(R.id.RecicleView_Task)
        btn_nuevaTarea = findViewById(R.id.btn_nuevaTarea)

        btn_nuevaTarea.setOnClickListener {
            adapter.add(Task(0, "Test", "Descripción", LocalDateTime.now()))
        }
    }

    private fun setAdapter() {
        adapter = TodoAdapter(task)

        RecicleView_Task.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        RecicleView_Task.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putParcelableArrayList("task", task as ArrayList<Task>)
        }
    }
}