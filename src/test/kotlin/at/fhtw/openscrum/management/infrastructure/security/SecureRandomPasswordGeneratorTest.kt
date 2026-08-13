package at.fhtw.openscrum.management.infrastructure.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SecureRandomPasswordGeneratorTest {
    lateinit var passwordGenerator: SecureRandomPasswordGenerator

    @BeforeEach
    fun setUp() {
        passwordGenerator = SecureRandomPasswordGenerator()
    }

    @Test
    fun ensureGeneratePasswordCreatesAnAlphanumericPasswordOfFixedLength() {
        // When
        val password = passwordGenerator.generatePassword()

        // Then
        assertThat(password).hasSize(16)
        assertThat(password).matches("[a-zA-Z0-9]+")
    }

    @Test
    fun ensureGeneratePasswordCreatesADifferentPasswordOnEveryCall() {
        // When
        val passwords = (1..10).map { passwordGenerator.generatePassword() }

        // Then
        assertThat(passwords).doesNotHaveDuplicates()
    }
}
