package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class PlanSprintCommand(
    val sprintGoal: String,
    val projectId: UUID,
    val sprintId: UUID,
    val productBacklogIds: Set<UUID>,
)
