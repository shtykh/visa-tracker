package shtykh.visa

import shtykh.visa.InOut.*
import java.time.temporal.ChronoUnit

class Visit(enter: SpaceTime, exit: SpaceTime) {

    val enter: SpaceTime = minOf(enter, exit)
    val exit: SpaceTime = maxOf(enter, exit)

    fun getDays(): Int = (enter.time.until(exit.time, ChronoUnit.DAYS) + 1).toInt()

    override fun toString(): String {
        return if (enter.space == exit.space) {
            "${enter.space},${enter.time.format()}-${exit.time.format()}"
        } else {
            "$enter-$exit"
        } + "(${getDays()}d)"
    }

    companion object {
        fun of(enter: Pair<String, String>, exit: Pair<String, String>) = Visit(SpaceTime(enter), SpaceTime(exit))
    }
}

open class SpaceTime(pair: Pair<String, String>) : Comparable<SpaceTime> {

    override fun compareTo(other: SpaceTime) = this.time.compareTo(other.time)
    val space = pair.first

    val time = pair.second.parseInstant()

    override fun toString(): String {
        return "$space,${time.format()}"
    }

}

class Stamp(pair: Pair<String, String>, val inOut: InOut? = null) : SpaceTime(pair)

enum class InOut { IN, OUT }

fun Array<Stamp>.toVisits(): Iterable<Visit> {
    val sorted = sorted()
    val visits = mutableListOf<Visit>()
    for (i in 0 until sorted.size / 2) {
        if (2 * i + 1 < size) {
            val enter = sorted[2 * i]
            val exit = sorted[2 * i + 1]
            enter.inOut?.let {
                if (it != IN) throw RuntimeException()
            }
            exit.inOut?.let {
                if (it != OUT) throw RuntimeException()
            }
            visits += Visit(enter, exit)
        }
    }
    return visits
}
