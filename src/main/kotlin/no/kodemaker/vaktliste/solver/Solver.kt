package no.kodemaker.vaktliste.solver

import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.solver.SolverConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.kodemaker.vaktliste.domain.Kodemaker
import no.kodemaker.vaktliste.domain.Timeslot
import no.kodemaker.vaktliste.domain.Shift
import no.kodemaker.vaktliste.domain.Roster
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

object Solver {
    private val logger = LoggerFactory.getLogger(Solver::class.java)


    fun solve():Roster {
        // 1. CONFIGURE SOLVER
        val solverFactory: SolverFactory<Roster> =  SolverFactory.create<Roster?>(
            SolverConfig()
                .withConstraintProviderClass(RosterConstraintProvider::class.java)
                .withSolutionClass(Roster::class.java)
                .withEntityClasses(Shift::class.java)
                .withTerminationSpentLimit(Duration.ofSeconds(1))
        )
        val solver = solverFactory.buildSolver()

        // 2. "LOAD" problem
        val problem = Roster(
            shifts = listOf(
                Shift(id ="Dag1.1.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0))),
                Shift(id ="Dag1.2.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0))),
                Shift(id ="Dag1.3.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0))),
                Shift(id ="Dag1.4.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(12, 0))),
                Shift(id ="Dag1.5.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0))),
                Shift(id ="Dag1.6.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0))),
                Shift(id ="Dag1.7.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(15, 0))),
                Shift(id ="Dag1.8.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0))),
                Shift(id ="Dag1.9.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(17, 0))),

                Shift(id ="Dag2.1.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 0))),
                Shift(id ="Dag2.2.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 0))),
                Shift(id ="Dag2.3.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(11, 0))),
                Shift(id ="Dag2.4.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(12, 0))),
                Shift(id ="Dag2.5.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 0))),
                Shift(id ="Dag2.6.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 0))),
                Shift(id ="Dag2.7.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(15, 0))),
                Shift(id ="Dag2.8.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(16, 0))),
                Shift(id ="Dag2.9.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(17, 0))),
            ),
            kodemakere = listOf(
                Kodemaker("mr", "Magnus Rundberget"),
                Kodemaker("amb", "André Bonkowski"),
                Kodemaker("fj", "Finn Johnsen"),
                Kodemaker("åt", "Åshild Thorrud"),
                Kodemaker("ov", "Olga Voronkova"),
                Kodemaker("stt", "Stein Tore Tøsse"),
                Kodemaker("fn", "Frode Nerbråten"),
                Kodemaker("km", "Kristoffer Moe"),
                Kodemaker("aks", "Alf Kristian Støyle"),
                Kodemaker("nl", "Nils Larsgård"),
                Kodemaker("msh", "Marina Santos Haugen"),
                Kodemaker("sg", "Sindre Grønningen"),
                Kodemaker("ew", "Eiving Waaler")
            ).shuffled()
        )


        // 3. Solve it !
        logger.info("Start solving problem")
        val solution = solver.solve(problem)
        logger.info("End solving problem")

        return solution
    }
}

fun main() = runBlocking {
    val res = Solver.solve()

    res.shifts.forEach {println(it)}

    val km1 = res.shifts.groupBy {
        it.kodemaker1
    }.mapValues { it.value.size }

    val km2 = res.shifts.groupBy {
        it.kodemaker2
    }.mapValues { it.value.size }


    delay(5000)
    println("\nVaktfordeling:")
    res.kodemakere.forEach {
        val numShifts = (km1[it] ?:0) + (km2[it] ?:0)
        println("${it.name.padEnd(25, ' ')} - $numShifts vakter" )
    }
}