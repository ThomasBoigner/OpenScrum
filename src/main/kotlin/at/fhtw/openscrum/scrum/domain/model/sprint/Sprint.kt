package at.fhtw.openscrum.scrum.domain.model.sprint

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class Sprint(
    val id: Long? = null,
    val sprintId: SprintId,
    val startDate: LocalDate = LocalDate.now(),
    endDate: LocalDate,
    status: SprintStatus = SprintStatus.NOT_PLANNED,
) {
    constructor(sprintId: SprintId, startDate: LocalDate = LocalDate.now(), sprintLength: Long) : this(
        sprintId = sprintId,
        startDate = startDate,
        endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).plusWeeks(sprintLength),
    )

    var endDate: LocalDate = endDate
        private set

    var status: SprintStatus = status
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
