package com.awtry.todoapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Task(
    val id: Int,
    val titulo: String,
    val description: String,
    val fecha: LocalDateTime
): Parcelable
