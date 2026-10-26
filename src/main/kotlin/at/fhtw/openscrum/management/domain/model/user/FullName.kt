package at.fhtw.openscrum.management.domain.model.user

data class FullName(
    val firstName: String,
    val lastName: String,
) {
    init {
        require(firstName.isNotBlank()) { "First name must not be blank!" }
        require(firstName.length <= 255) { "First name must not be longer than 255 characters!" }
        require(lastName.isNotBlank()) { "Last name must not be blank!" }
        require(lastName.length <= 255) { "Last name must not be longer than 255 characters!" }
    }

    val fullName: String = "$firstName $lastName"
}
