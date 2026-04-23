package at.fhtw.openscrum.scrum.application.dtos

enum class ProductBacklogItemStatusDto(val displayName: String) {
    IN_BACKLOG("In backlog"),
    COMMITED_TO_SPRINT("Commited to sprint"),
    DONE("Done"),
}
