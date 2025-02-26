package no.kodemaker.vaktliste.solver

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import ai.timefold.solver.core.api.score.stream.Joiners
import no.kodemaker.vaktliste.domain.ShiftAssignment

class RosterConstraintProvider : ConstraintProvider {
    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            mustHaveDifferentEmployees(constraintFactory),
            mustNotBeAssignedToBlockedOutSlot(constraintFactory),
            shouldHaveAsFewShiftsAsPossiblePerDay(constraintFactory),
            shouldHaveDifferentEmployeeCombos(constraintFactory),

            )
    }

    private fun mustHaveDifferentEmployees(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEachUniquePair(
                ShiftAssignment::class.java,
                Joiners.equal(ShiftAssignment::shift),
                Joiners.equal(ShiftAssignment::employee)
            )
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Must be different Employees in a shift")

    private fun mustNotBeAssignedToBlockedOutSlot(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(ShiftAssignment::class.java)
            .filter {
                it.employee?.blockedSlots?.contains(it.shift.timeslot) ?: false
            }.penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Employee must be available for timeslot in shift")

    private fun shouldHaveAsFewShiftsAsPossiblePerDay(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEachUniquePair(
            ShiftAssignment::class.java,
            Joiners.equal(ShiftAssignment::employee),
            Joiners.equal { it.shift.timeslot.dayOfWeek },
        ).penalize(HardSoftScore.ONE_SOFT)
            .asConstraint("Penalize multiple timeslots per day")


    private fun shouldHaveDifferentEmployeeCombos(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEachUniquePair(
            ShiftAssignment::class.java,
            Joiners.equal(ShiftAssignment::shift)
        ).map { a, b -> setOf(a.employee, b.employee) }
            .groupBy({ it }, ConstraintCollectors.count())
            .filter { _, count -> count > 1 }
            .penalize(HardSoftScore.ONE_SOFT, { _, count -> count })
            .asConstraint("Penalize having same combo of employees for a shift more than once")


}