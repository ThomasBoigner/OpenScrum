package at.fhtw.openscrum.scrum.domain.model.project

data class SprintLength(
    val length: Int,
) {
    init {
        require(length > 0) { "Sprint length must not be shorter than 0 Weeks!" }
        require(length <= 4) { "Sprint length must not be longer than 4 Weeks!" }
    }
}
