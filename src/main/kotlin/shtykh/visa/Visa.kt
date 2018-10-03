package shtykh.visa

import java.time.Instant

abstract class Visa(val number: String, val duration: Int, val countryOfIssue: String, val start: Instant, val end: Instant) {
    override fun toString(): String {
        return "$countryOfIssue visa #$number $duration days ${start.format()} till ${end.format()}:\n${visitsReport()}"
    }

    abstract fun isValid(visit: Visit): Boolean

    abstract fun visitsReport(): String

    abstract fun add(visit: Visit)
}

class OnceSchengen(number: String, duration: Int, countryOfIssue: String, start: String, end: String) : Visa(number, duration, countryOfIssue, start.parseInstant(), end.parseInstant()) {
    private var visit: Visit? = null

    override fun add(visit: Visit) {
        this.visit = visit
    }

    override fun isValid(visit: Visit) = when {
        this.visit != null -> false
        visit.getDays() > duration -> false
        visit.enter.time.isBefore(start) -> false
        visit.exit.time.isAfter(end) -> false
        else -> true
    }

    override fun visitsReport(): String = visit.toString()
}
