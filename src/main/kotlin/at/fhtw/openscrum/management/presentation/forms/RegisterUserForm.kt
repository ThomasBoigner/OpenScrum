package at.fhtw.openscrum.management.presentation.forms

import at.fhtw.openscrum.management.application.command.RegisterUserCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserForm(
    @NotBlank(message = "Username must not be blank!")
    @Size(max = 255, message = "Username must not be longer than 255 characters!")
    var username: String = "",
    @NotBlank(message = "Email address must not be blank!")
    @Email(message = "Email address must be valid!")
    @Size(max = 255, message = "Email address must not be longer than 255 characters!")
    var email: String = "",
    @NotBlank(message = "First name must not be blank!")
    @Size(max = 255, message = "First name must not be longer than 255 characters!")
    var firstName: String = "",
    @NotBlank(message = "Last name must not be blank!")
    @Size(max = 255, message = "Last name must not be longer than 255 characters!")
    var lastName: String = "",
    @NotBlank(message = "Password must not be blank!")
    @Size(max = 255, message = "Password must not be longer than 255 characters!")
    var password: String = "",
) {
    fun toRegisterUserCommand(): RegisterUserCommand =
        RegisterUserCommand(
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
        )
}
