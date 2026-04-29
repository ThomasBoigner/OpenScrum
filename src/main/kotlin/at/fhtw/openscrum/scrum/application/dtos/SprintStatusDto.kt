package at.fhtw.openscrum.scrum.application.dtos

enum class SprintStatusDto(
    val displayName: String,
) {
    NOT_PLANNED("Not Planned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
}
