package at.fhtw.openscrum.management.application.command

class RegisterUserCommand(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
) {
    constructor(): this("", "", "", "", "")
}
