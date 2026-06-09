package com.example.noteslist.domain.usecase

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormatDateUseCase {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun execute(date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> formatter.format(date)
        }
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return dateTime.format(formatter)
    }
}