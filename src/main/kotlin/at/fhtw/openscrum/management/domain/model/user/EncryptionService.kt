package at.fhtw.openscrum.management.domain.model.user

interface EncryptionService {
    fun hashPassword(password: String): String?
}
