package com.awtry.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        val NEW_TASK = 200
        val NEW_TASK_LLAVE = "newTask"
        val UPDATE_TASK = 201

    }

    private lateinit var RecicleView_Task: RecyclerView
    private lateinit var btn_nuevaTarea: FloatingActionButton

    private val SAVED_TASK = "task"

    private lateinit var adapter: TodoAdapter
    private lateinit var db: TaskDatabase


    private var task = mutableListOf<Task>()

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

        createNotificationChannel()
        //NotificationManagerImp().createNotificationChannel(this)
        initViews()
        //setAdapter()
    }


    override fun onResume() {
        super.onResume()
        db = Room.databaseBuilder(this, TaskDatabase::class.java, "Task").build()

        MainScope().launch {
            task = db.taskDAO().getPendingTask().toMutableList()
            setAdapter()
        }
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
        adapter = TodoAdapter(task, onClickDoneTask = { task, position ->
            MainScope().launch {
                db.taskDAO().updateTask(task.apply {
                    status = false
                })
                adapter.remove(position)
            }
            stopNotification(this, task.id)
        }, onClickDetailTask = { task ->
            startActivityForResult(Intent(this, FormActivity::class.java).apply {
                putExtra("isTaskDetail", true)
                putExtra("task", task)
            }, UPDATE_TASK)
        })

        RecicleView_Task.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        RecicleView_Task.adapter = adapter
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //Aplicamos los cambios que se hagan, agregando las tareas.
        outState.apply {
            putParcelableArrayList(SAVED_TASK, task as ArrayList<Task>)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_TASK) {
            //Asegurar que la tarea no viene vacía para poder agregar los datos
            data?.getParcelableExtra<Task>(NEW_TASK_LLAVE)?.let {
                MainScope().launch(Dispatchers.Main) {
                    adapter.add(it)
                    //NotificationManagerImp().createNotification(this@MainActivity, it)
                }

                MainScope().launch(Dispatchers.IO) {
                    //db.taskDAO().saveNewTask(it)

                    val zone = OffsetDateTime.now().offset
                    val selectedMilis = it.fecha?.toInstant(zone)?.toEpochMilli() ?: 0
                    val nowMilis = LocalDateTime.now().toInstant(zone).toEpochMilli() ?: 0

                    scheduleNotification(selectedMilis - nowMilis, Data.Builder().apply {
                        putInt("notificationID", db.taskDAO().saveNewTask(it).toInt())
                        putString("notificationTitle", it.titulo)
                        putString("notificationDescription", it.description)
                    }.build())
                }
            }
        } else if (requestCode == UPDATE_TASK) {
            data?.getParcelableExtra<Task>(NEW_TASK_LLAVE)?.let {
                MainScope().launch(Dispatchers.Main) {
                    adapter.update(it)
                }
                MainScope().launch(Dispatchers.IO) {
                    db.taskDAO().updateTask(it)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TASK"
            val descriptionText = "Channel of pending tasks"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)


        }
    }

    private fun scheduleNotification(delay: Long, data: Data) {

        val notificationWork = OneTimeWorkRequest.Builder(NotificationManagerImp::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            "NOTIFICATION_WORK ${data.getInt("notificationID", 0)}",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            notificationWork
        ).enqueue()

    }

    private fun stopNotification(context: Context, idNotification: Int) {
        val stopNoti = WorkManager.getInstance(context)
        stopNoti.cancelUniqueWork("NOTIFICATION_WORK ${idNotification}")
    }

}