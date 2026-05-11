package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class ScheduleSprintCommand(
    val projectId: UUID,
)
