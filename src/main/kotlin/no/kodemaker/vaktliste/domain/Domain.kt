package no.kodemaker.vaktliste.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import java.time.DayOfWeek
import java.time.LocalTime

data class Timeslot(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime = startTime.plusMinutes(50)
) {
    override fun toString(): String = dayOfWeek.toString().padEnd(10, ' ') + startTime.toString()
}

data class Employee(
    val id: String,
    val name: String,
    val blockedSlots: List<Timeslot> = listOf(),
)


data class Shift(
    val id: String,
    val timeslot: Timeslot
)

@PlanningEntity
data class ShiftAssignment(
    @PlanningId
    val id: String,

    val shift: Shift,
    val shiftIdx: Int,

    @PlanningVariable
    var employee: Employee? = null,
) {
    // NOTE: Timefold requires a no-arg constructor (:
    @Suppress("unused")
    constructor() : this(
        "dummy[0]",
        Shift(id="dummy", timeslot = Timeslot(DayOfWeek.SUNDAY, LocalTime.of(0, 0))),
        0
    )
}


@PlanningSolution
data class Roster(
    // These are the entities we want timefold to plan for us
    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    val shiftAssignments: List<ShiftAssignment> = emptyList(),

    // These are the facts available for the solution
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    val employees: List<Employee> = emptyList(),

    @PlanningScore
    var score: HardSoftScore? = null,
)