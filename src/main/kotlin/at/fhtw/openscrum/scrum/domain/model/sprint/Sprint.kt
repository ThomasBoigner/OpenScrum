package at.fhtw.openscrum.scrum.domain.model.sprint

import java.time.LocalDate

class Sprint(
    val id: Long? = null,
    val sprintId: SprintId,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: SprintStatus,
)
