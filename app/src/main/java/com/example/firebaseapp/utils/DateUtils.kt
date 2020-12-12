package com.example.firebaseapp.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_PATTERN = "yyyy-MM-dd"

class DateUtils {

    fun mapToNormalisedDateText(timestamp: Long) : String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.US)

        return formatter.format(date)
    }
}