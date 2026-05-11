package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

enum class ProductBacklogItemStatus(
    val isCommitedToSprint: Boolean,
) {
    IN_BACKLOG(false),
    COMMITTED_TO_SPRINT(true),
    COMMITTED_TO_SPRINT_DONE(true),
    DONE(false),
}
