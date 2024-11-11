package com.overdevx.reservationapp.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// Helper function to format date
fun formatDate(dateString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(parser.parse(dateString) ?: Date())
}

fun convertDate(inputDate: String): String? {
    return try {
        // Parse the input date string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC timezone for the input format
        val date = inputFormat.parse(inputDate)

        // Format to the desired output string
        val outputFormat = SimpleDateFormat("EEEE, dd MMM yy", Locale("id", "ID"))
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun convertDate2(inputDate: String): String? {
    return try {
        // Parse the input date string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC timezone for the input format
        val date = inputFormat.parse(inputDate)

        // Format to the desired output string
        val outputFormat = SimpleDateFormat("EEEE, dd MMM yy", Locale("id", "ID"))
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun formatCurrency(value: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return formatter.format(value)
}

fun replaceDomain(url: String, newDomain: String): String {
    return url.replace("localhost", newDomain)
}
