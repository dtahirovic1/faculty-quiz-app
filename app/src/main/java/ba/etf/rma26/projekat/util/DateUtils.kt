package ba.etf.rma26.projekat.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    fun parseApiDate(value: String): LocalDate? {
        return try {
            LocalDate.parse(value.take(10))
        } catch (_: Exception) {
            null
        }
    }

    fun formatApiDate(value: String): String {
        val date = parseApiDate(value)
        return date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: value.take(10)
    }
}
