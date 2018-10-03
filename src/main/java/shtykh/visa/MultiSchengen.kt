package shtykh.visa

import java.time.Instant

class MultiSchengen(number: String, duration: Int, countryOfIssue: String, start: String, end: String) : Visa(number, duration, countryOfIssue, start.parseInstant(), end.parseInstant()) {

    private val periods = mutableMapOf<Instant, MultiPeriod>()

    init {
        var nextPeriodStart = this.start
        while (nextPeriodStart.isBefore(this.end)) {
            val periodStart = nextPeriodStart
            nextPeriodStart = minOf(nextPeriodStart.addHalfYear(), this.end)
            periods[nextPeriodStart] = MultiPeriod(number, duration, countryOfIssue, periodStart, nextPeriodStart)
        }

    }

    override fun add(visit: Visit) {
        val endPeriod = periods.keys.first { it.isAfter(visit.exit.time) }
        periods[endPeriod]?.add(visit)
    }

    override fun isValid(visit: Visit): Boolean = periods.filterValues { it.isValid(visit) }.isNotEmpty()

    override fun visitsReport(): String = periods.map { "--before ${it.key.format()}(${it.value.getDays()}d):\n${it.value.visitsReport()}" }.joinToString(separator = ";\n")

}

private class MultiPeriod(number: String, duration: Int, countryOfIssue: String, start: Instant, end: Instant) : Visa(number, duration, countryOfIssue, start, end) {

    private val visits = mutableListOf<Visit>()

    override fun add(visit: Visit) {
        visits += visit
    }

    override fun isValid(visit: Visit): Boolean {
        val sumBy: Int = visits.sumBy { it.getDays() }
        return when {
            visit.getDays() > duration -> false
            visit.enter.time.isBefore(start) -> false
            visit.exit.time.isAfter(end) -> false
            sumBy + visit.getDays() > duration -> false
            else -> true
        }
    }

    override fun visitsReport(): String {
        return visits.takeUnless { it.isEmpty() }?.joinToString(separator = ",\n") ?: "none"
    }

    fun getDays() = visits.map { it.getDays() }.sum()

}
