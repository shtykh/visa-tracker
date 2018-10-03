package shtykh.visa

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MultiSchengenTest {

    var visa: Visa? = null

    @Before
    fun init() {
        visa = MultiSchengen(
            number = "01234",
            countryOfIssue = "France",
            start = "28.04.18",
            end = "27.04.20",
            duration = 90
        )
    }

    @Test
    fun add() {
        val stamps = arrayOf(
            Stamp("Paris" to "28.04.18"),
            Stamp("Paris" to "30.04.18"),
            Stamp("Geneve" to "24.06.18"),
            Stamp("Barcelona" to "18.05.18")
        )
        val visits = stamps.toVisits()
        assertEquals(2, visits.toList().size)
        visa?.let {
            with(it) {
                visits.forEach(::add)
                print(toString())
            }
        }
    }

    @Test
    fun isValid() {
        add()
        visa?.let {
            with(it) {
                assertFalse(isValid(Visit.of("Berlin" to "01.01.2010", "Berlin" to "01.01.2020")))
            }
        }
    }

    @Test
    fun visitsReport() {
    }

}
