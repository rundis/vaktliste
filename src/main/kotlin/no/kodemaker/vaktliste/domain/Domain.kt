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

data class Kodemaker(
    val id: String,
    val name: String,
)

@PlanningEntity
data class Shift(
    @PlanningId
    val id: String,
    val timeslot: Timeslot
) {
    // Timefold will set/modify these 2 during constructions and local search
    @PlanningVariable
    var kodemaker1: Kodemaker? = null

    @PlanningVariable
    var kodemaker2: Kodemaker? = null

    // NOTE: Timefold requires a no-arg constructor (:
    @Suppress("unused")
    constructor() : this(
        "dummy",
        Timeslot(DayOfWeek.SUNDAY, LocalTime.of(0, 0))
    )

    override fun toString(): String =
        "$timeslot: ${kodemaker1?.name?.padEnd(25, ' ')} ${kodemaker2?.name}"

}


@PlanningSolution
data class Roster(
    // These are the entities we want timefold to plan for us
    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    val shifts: List<Shift> = emptyList(),

    // These are the facts available for the solution
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    val kodemakere: List<Kodemaker> = emptyList(),

    @PlanningScore
    var score: HardSoftScore? = null,
)