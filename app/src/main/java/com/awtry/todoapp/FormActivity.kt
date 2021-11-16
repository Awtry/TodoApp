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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class FormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtTime: EditText
    private lateinit var btn_GuardarTarea: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        //Sección de agregar dato martillado
        /*setResult(
            NEW_TASK,
            Intent().putExtra(NEW_TASK_LLAVE, Task(0, "Test", "Descripción", LocalDateTime.now()))
        )*/

        //Llamada a los campos especificos para obtener su info
        initViews()
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
                    edtDate.setText("$day/$month/$year")
                },
                nowDate.year,
                nowDate.monthValue - 1,
                nowDate.dayOfMonth
            )

            calendario.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
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
                    NEW_TASK,
                    Intent().putExtra(
                        NEW_TASK_LLAVE,
                        Task(
                            0,
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