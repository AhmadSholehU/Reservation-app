package com.overdevx.reservationapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Helper function to format date
fun formatDate(dateString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(parser.parse(dateString) ?: Date())
}
