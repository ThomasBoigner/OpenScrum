package at.fhtw.openscrum.management.domain.model.user

interface PasswordGenerator {
    fun generatePassword(): String
}
