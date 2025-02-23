package no.kodemaker.vaktliste.solver

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import no.kodemaker.vaktliste.domain.Shift

class RosterConstraintProvider : ConstraintProvider {
    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            shiftMustHaveDifferentKodemakere(constraintFactory),
            fewerShiftsPlease(constraintFactory),
            meetNewKodemakerePlease(constraintFactory),
        )
    }

    private fun shiftMustHaveDifferentKodemakere(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(Shift::class.java)
            .filter { it.kodemaker1 == it.kodemaker2 }
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Must be different Kodemakers in a shift")

    private fun fewerShiftsPlease(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEachUniquePair(Shift::class.java)
            .filter { vakt1, vakt2 ->
                vakt1.kodemaker1 == vakt2.kodemaker1 || vakt1.kodemaker1 == vakt2.kodemaker2 || vakt1.kodemaker2 == vakt2.kodemaker1 || vakt1.kodemaker2 == vakt2.kodemaker2
            }
            .penalize(HardSoftScore.ONE_SOFT)
            .asConstraint("Reward fewer shifts per Kodemaker (aka punish more shifts ...)")

    private fun meetNewKodemakerePlease(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEachUniquePair(Shift::class.java)
            .filter { vakt1, vakt2 ->
                setOf(vakt1.kodemaker1, vakt1.kodemaker2) == setOf(vakt2.kodemaker1, vakt2.kodemaker2)
            }
            .penalize(HardSoftScore.ofSoft(3))
            .asConstraint("Meeting new Kodemakers is a good thing (aka punish shift with same two Kodemakers")
}