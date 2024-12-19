package com.overdevx.reservationapp.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isUnspecified


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

fun reverseConvertDate(inputDate: String): String? {
    return try {
        // Format input date string: "Kamis, 26 Des 24" (bahasa Indonesia)
        val inputFormat = SimpleDateFormat("EEEE, dd MMM yy", Locale("id", "ID"))
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Timezone tetap konsisten

        // Parse input string ke dalam Date object
        val date = inputFormat.parse(inputDate)

        // Format ke output ISO: "yyyy-MM-dd"
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
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

@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle = MaterialTheme.typography.body1,
    modifier: Modifier = Modifier,
    color: Color = style.color
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.body1.fontSize

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        softWrap = true,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = defaultFontSize
                    )
                }
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}

@Composable
fun getDpi(): Float {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density
    return configuration.screenWidthDp * density
}
