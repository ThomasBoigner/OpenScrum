package at.fhtw.openscrum.management.presentation.forms

import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterUserForm(
    @NotBlank(message = "Username must not be blank!")
    val username: String,
    @NotBlank(message = "Email address must not be blank!")
    @Email(message = "Email address must be valid!")
    val email: String,
    @NotBlank(message = "First name must not be blank!")
    val firstName: String,
    @NotBlank(message = "Last name must not be blank!")
    val lastName: String,
    @NotBlank(message = "Password must not be blank!")
    val password: String,
) {
    constructor() : this("", "", "", "", "")

    fun toRegisterUserCommand(): RegisterUserCommand =
        RegisterUserCommand(
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
        )
}
