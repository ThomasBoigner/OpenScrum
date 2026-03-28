package at.fhtw.openscrum.management.domain.model.user

data class FullName(
    val firstName: String,
    val lastName: String,
) {
    init {
        require(firstName.isNotBlank()) { "First name must not be blank!" }
        require(firstName.length < 128) { "First name must not be longer than 128 characters!" }
        require(lastName.isNotBlank()) { "Last name must not be blank!" }
        require(lastName.length < 128) { "Last name must not be longer than 128 characters!" }
    }

    val fullName: String = "$firstName $lastName"
}
