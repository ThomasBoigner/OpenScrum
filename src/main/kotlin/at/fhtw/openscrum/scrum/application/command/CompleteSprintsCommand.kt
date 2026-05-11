package at.fhtw.openscrum.scrum.application.command

import java.time.LocalDate

data class CompleteSprintsCommand(
    val date: LocalDate,
)
