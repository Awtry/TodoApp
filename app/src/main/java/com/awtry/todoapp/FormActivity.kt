package com.awtry.todoapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.awtry.todoapp.MainActivity.Companion.NEW_TASK
import com.awtry.todoapp.MainActivity.Companion.NEW_TASK_LLAVE
import com.awtry.todoapp.MainActivity.Companion.UPDATE_TASK
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class FormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtTime: EditText
    private lateinit var btn_GuardarTarea: Button

    private var isDetailTask = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        isDetailTask = intent.getBooleanExtra("isDetailTask", false)


        //Sección de agregar dato martillado
        /*setResult(
            NEW_TASK,
            Intent().putExtra(NEW_TASK_LLAVE, Task(0, "Test", "Descripción", LocalDateTime.now()))
        )*/

        //Llamada a los campos especificos para obtener su info
        initViews()
        //if (isDetailTask) setTaskInfo(intent.getParcelableExtra())
        if (isDetailTask) setTaskInfo(intent.getParcelableExtra("task") ?: Task())
    }

    private fun setTaskInfo(task: Task) {
        edtTitle.setText(task.titulo)
        edtDescription.setText(task.description)
        edtDate.setText(task.fecha?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        edtTime.setText(task.fecha?.format(DateTimeFormatter.ofPattern("HH:mm")))

        //Boton de uptate
        btn_GuardarTarea.text = "update-me"
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        edtDate = findViewById(R.id.edtDate)
        edtTime = findViewById(R.id.edtTime)
        btn_GuardarTarea = findViewById(R.id.btn_GuardarTarea)

        edtDate.setOnClickListener {
            val nowDate = LocalDate.now()

            var calendario = DatePickerDialog(
                this,
                { _, year, month, day ->
                    /*if (month == 0){
                        var auxMont = 1
                        edtDate.setText("$day/$auxMont/$year")
                    }else{

                    }*/
                    //edtDate.setText("$if(day < 01) "$day/$month/$year")
                    //edtDate.setText("$day/$month/$year")
                    edtDate.setText(
                        "${if (day < 10) "0$day" else day}/${
                            if (month < 10) {
                                "0${month + 1}"
                            } else {
                                month + 1
                            }
                        }/$year"
                    )
                },
                nowDate.year,
                nowDate.monthValue - 1,
                nowDate.dayOfMonth
            )

            calendario.getDatePicker().setMinDate(Calendar.getInstance().timeInMillis)
            //calendario.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            calendario.show()

        }

        edtTime.setOnClickListener {
            val nowTime = LocalTime.now()


            TimePickerDialog(
                this,
                { _, hour, minute ->
                    var hora = if (hour.toString().length > 1) {
                        hour.toString()
                    } else {
                        "0$hour"
                    }

                    var minuto = if (minute.toString().length > 1) {
                        minute.toString()
                    } else {
                        "0$minute"
                    }

                    edtTime.setText("$hora:$minuto")
                    //edtTime.setText("$hour:$minute")
                },
                nowTime.hour,
                nowTime.minute,
                true
            ).show()
        }

        btn_GuardarTarea.setOnClickListener {

            if (edtTitle.text.toString().trim().isEmpty() || edtDescription.text.toString().trim()
                    .isEmpty() || edtTime.text.toString().trim()
                    .isEmpty() || edtDate.text.toString().trim().isEmpty()
            ) {
                //Avisar al usuario que le falta rellenar todos los huecos
                Toast.makeText(
                    this,
                    "Llename todo por favor uwur.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setResult(
                    if (isDetailTask) UPDATE_TASK else NEW_TASK,
                    Intent().putExtra(
                        NEW_TASK_LLAVE,
                        Task(
                            intent.getParcelableExtra<Task>("task")?.id ?: 0,
                            edtTitle.text.toString(),
                            edtDescription.text.toString(),
                            //Entregamos ya el objeto con la fecha y le damos un formato de salida.
                            LocalDateTime.of(
                                LocalDate.parse(
                                    edtDate.text,
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                ),
                                LocalTime.parse(
                                    edtTime.text,
                                    DateTimeFormatter.ofPattern("HH:mm")
                                )
                            )
                        )
                    )
                )

                //Para que ya una vez completada la recolección de datos se regrese a la vista anterior
                finish()
            }


        }
    }
}