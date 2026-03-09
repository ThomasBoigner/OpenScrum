package at.fhtw.openscrum.management.infrastructure.security

import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockitoExtension::class)
class CustomUserDetailsServiceTest {
    lateinit var userDetailsService: CustomUserDetailsService

    @Mock
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userDetailsService = CustomUserDetailsService(userRepository)
    }

    @Test
    fun ensureLoadByUsernameWorksProperly() {
        // Given
        val email = "john.doe@gmail.com"
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress(email),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )
        whenever(userRepository.findByEmail(email)).thenReturn(user)

        // When
        val userDetails = userDetailsService.loadUserByUsername(email)

        // Then
        assertThat(userDetails.username).isEqualTo(email)
        assertThat(userDetails.password).isEqualTo(user.password)
        assertThat(userDetails.isEnabled).isTrue
        assertThat(userDetails.isAccountNonExpired).isTrue
        assertThat(userDetails.isCredentialsNonExpired).isTrue
        assertThat(userDetails.isCredentialsNonExpired).isTrue
        assertThat(userDetails.isAccountNonLocked).isTrue
        assertThat(userDetails.authorities).contains(SimpleGrantedAuthority("USER"))
    }

    @Test
    fun ensureLoadByUsernameThrowsExceptionWhenUserCanNotBeFound() {
        // Given
        val email = "john.doe@gmail.com"
        val user =
            User(
                username = "john.doe",
                emailAddress = EmailAddress(email),
                fullName = FullName("John", "Doe"),
                password = "abc123",
                role = Role.USER,
            )
        whenever(userRepository.findByEmail(email)).thenReturn(null)

        // When
        assertThrows<UsernameNotFoundException> { userDetailsService.loadUserByUsername(email) }
    }
}
