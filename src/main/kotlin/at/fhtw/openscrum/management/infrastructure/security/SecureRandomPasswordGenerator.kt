package at.fhtw.openscrum.management.infrastructure.security

import at.fhtw.openscrum.management.domain.model.user.PasswordGenerator
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class SecureRandomPasswordGenerator : PasswordGenerator {
    private val random = SecureRandom()

    override fun generatePassword(): String =
        (1..PASSWORD_LENGTH)
            .map { ALPHABET[random.nextInt(ALPHABET.size)] }
            .joinToString("")

    companion object {
        private const val PASSWORD_LENGTH = 16
        private val ALPHABET = ('a'..'z') + ('A'..'Z').toList() + ('0'..'9').toList()
    }
}
