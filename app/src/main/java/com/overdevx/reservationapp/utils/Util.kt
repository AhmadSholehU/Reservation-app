package com.overdevx.reservationapp.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Helper function to format date
fun formatDate(dateString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(parser.parse(dateString) ?: Date())
}

fun convertDate(selectedDate: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(selectedDate)
    return dateFormat.format(date)
}

fun formatCurrency(value: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return formatter.format(value)
}
