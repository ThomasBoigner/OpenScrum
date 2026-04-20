package at.fhtw.openscrum.scrum.application.command

import java.util.UUID

data class DefineDefinitionOfDoneCommand(
    val projectId: UUID,
    val definitionOfDone: String
)
