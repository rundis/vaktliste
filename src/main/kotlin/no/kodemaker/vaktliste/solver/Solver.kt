package no.kodemaker.vaktliste.solver

import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.solver.SolverConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.kodemaker.vaktliste.domain.*
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

object Solver {
    private val logger = LoggerFactory.getLogger(Solver::class.java)


    fun solve(): Roster {
        // 1. CONFIGURE SOLVER
        val solverFactory: SolverFactory<Roster> = SolverFactory.create<Roster?>(
            SolverConfig()
                .withConstraintProviderClass(RosterConstraintProvider::class.java)
                .withSolutionClass(Roster::class.java)
                .withEntityClasses(ShiftAssignment::class.java)
                .withTerminationSpentLimit(Duration.ofSeconds(1))
        )
        val solver = solverFactory.buildSolver()

        // 2. "LOAD" problem
        val shifts = listOf(
            Shift(id = "Dag1.1.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0))),
            Shift(id = "Dag1.2.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0))),
            Shift(id = "Dag1.3.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0))),
            Shift(id = "Dag1.4.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(12, 0))),
            Shift(id = "Dag1.5.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0))),
            Shift(id = "Dag1.6.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0))),
            Shift(id = "Dag1.7.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(15, 0))),
            Shift(id = "Dag1.8.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0))),
            Shift(id = "Dag1.9.Vakt", timeslot = Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(17, 0))),

            Shift(id = "Dag2.1.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 0))),
            Shift(id = "Dag2.2.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 0))),
            Shift(id = "Dag2.3.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(11, 0))),
            Shift(id = "Dag2.4.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(12, 0))),
            Shift(id = "Dag2.5.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 0))),
            Shift(id = "Dag2.6.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 0))),
            Shift(id = "Dag2.7.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(15, 0))),
            Shift(id = "Dag2.8.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(16, 0))),
            Shift(id = "Dag2.9.Vakt", timeslot = Timeslot(DayOfWeek.THURSDAY, LocalTime.of(17, 0))),
        )


        val problem = Roster(

            shifts.flatMap {
                listOf(
                    ShiftAssignment(id = "$it.id}[0]", shift = it, shiftIdx = 1),
                    ShiftAssignment(id = "$it.id}[1]", shift = it, shiftIdx = 2),
                )
            },
            employees = listOf(
                Employee(
                    "mr", "Magnus Rundberget", blockedSlots = listOf(
                        Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 0)),
                        Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 0)),
                        Timeslot(DayOfWeek.THURSDAY, LocalTime.of(11, 0))
                    )
                ),
                Employee(
                    "amb", "André Bonkowski", blockedSlots = listOf(
                        Timeslot(DayOfWeek.THURSDAY, LocalTime.of(15, 0)),
                        Timeslot(DayOfWeek.THURSDAY, LocalTime.of(16, 0)),
                        Timeslot(DayOfWeek.THURSDAY, LocalTime.of(17, 0)),
                    )
                ),
                Employee("fj", "Finn Johnsen"),
                Employee("åt", "Åshild Thorrud"),
                Employee("ov", "Olga Voronkova"),
                Employee("stt", "Stein Tore Tøsse"),
                Employee("fn", "Frode Nerbråten"),
                Employee("km", "Kristoffer Moe"),
                Employee("aks", "Alf Kristian Støyle"),
                Employee("nl", "Nils Larsgård"),
                Employee("msh", "Marina Santos Haugen"),
                Employee("sg", "Sindre Grønningen"),
                Employee("ew", "Eivind Waaler"),
                Employee("sm", "Stig Melling"),
                Employee("kf", "Kristian Frølich"),
                Employee("fa", "Fredrik Aubert"),
                Employee("af", "Anders Furseth"),
                Employee("rl", "Ronny Løvtangen")


            )//.shuffled()
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

    res.shiftAssignments.groupBy { it.shift.timeslot }.forEach {
        println("${it.key}: ${it.value[0].employee?.name?.padEnd(25, ' ')} - ${it.value[1].employee?.name}")
    }

    val countByKm =
        res.shiftAssignments
            .groupBy { it.employee }
            .mapValues { it.value.size }




    delay(5000)
    println("\nVaktfordeling:")
    res.employees.forEach {
        val numShifts = countByKm[it] ?: 0
        println("${it.name.padEnd(25, ' ')} - $numShifts vakter")
    }
}