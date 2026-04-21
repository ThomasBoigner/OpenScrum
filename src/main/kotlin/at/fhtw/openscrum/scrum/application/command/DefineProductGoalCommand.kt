package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class DefineProductGoalCommand(
    val projectId: UUID,
    val productGoal: String,
)
