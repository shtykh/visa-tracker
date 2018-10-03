package shtykh.visa

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.Calendar.*

private var dateFormat = SimpleDateFormat("dd.MM.yy")

fun Instant.format() = dateFormat.format(Date.from(this))

fun String.parseInstant(): Instant = dateFormat.parse(this).toInstant()

fun Instant.addHalfYear(): Instant {
    val months = 6
    return Calendar.getInstance().also {
        it.timeInMillis = this.toEpochMilli()
        val oldMonth = it[MONTH]
        val newMonth = (oldMonth + months) % 12
        it[MONTH] = newMonth
        if (newMonth < oldMonth) {
            it[YEAR] = it[YEAR] + 1
        }
    }.toInstant()
}
