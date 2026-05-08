package at.fhtw.openscrum.scrum.domain.model.sprint

enum class SprintBacklogItemStatus(
    val canMoveLeft: Boolean,
    val canMoveRight: Boolean,
) {
    TO_DO(false, true),
    IN_PROGRESS(true, true),
    DONE(true, false),
}
