package at.fhtw.openscrum.management.infrastructure.security

import at.fhtw.openscrum.management.domain.model.user.EncryptionService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SpringSecurityEncryptionService(
    private val encoder: PasswordEncoder,
) : EncryptionService {
    override fun hashPassword(password: String): String? = encoder.encode(password)
}
