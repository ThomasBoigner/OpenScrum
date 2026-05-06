package at.fhtw.openscrum.scrum.domain.model.sprint

enum class SprintStatus(
    val isPlanned: Boolean,
) {
    NOT_PLANNED(false),
    IN_PROGRESS(true),
    COMPLETED(true),
    CANCELLED(true),
}
