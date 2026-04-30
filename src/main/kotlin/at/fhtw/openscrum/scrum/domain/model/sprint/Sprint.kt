package at.fhtw.openscrum.scrum.domain.model.sprint

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class Sprint(
    val id: Long? = null,
    val sprintId: SprintId,
    val sprintName: String,
    val startDate: LocalDate = LocalDate.now(),
    endDate: LocalDate,
    status: SprintStatus = SprintStatus.NOT_PLANNED,
    sprintGoal: String? = null,
) {
    constructor(sprintId: SprintId, sprintNumber: Int, startDate: LocalDate = LocalDate.now(), sprintLength: Long) : this(
        sprintId = sprintId,
        sprintName = "Sprint $sprintNumber",
        startDate = startDate,
        endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).plusWeeks(sprintLength).minusDays(1),
    )

    var endDate: LocalDate = endDate
        private set

    var status: SprintStatus = status
        private set

    var sprintGoal: String? = sprintGoal
        private set

    override fun toString(): String = "Sprint(sprintId=$sprintId, startDate=$startDate, endDate=$endDate, status=$status)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sprint

        return sprintId == other.sprintId
    }

    override fun hashCode(): Int = sprintId.hashCode()
}
