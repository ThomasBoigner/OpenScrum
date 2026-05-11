package at.fhtw.openscrum.scrum.domain.model.sprint

enum class SprintStatus(
    val isPlanned: Boolean,
    val isFinished: Boolean,
) {
    NOT_PLANNED(false, false),
    IN_PROGRESS(true, false),
    COMPLETED(true, true),
    CANCELLED(true, true),
}
