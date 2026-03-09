package at.fhtw.openscrum.management.infrastructure.security

import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("userDetailsService")
@Transactional
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    private val log: Logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java),
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        log.debug("trying to load user with username: {}", username)

        val user: User =
            userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("No user found with username: $username")
        val enabled = true
        val accountNonExpired = true
        val credentialsNonExpired = true
        val accountNonLocked = true

        log.debug("Logged in as user {}", username)
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            enabled,
            accountNonExpired,
            credentialsNonExpired,
            accountNonLocked,
            listOf(SimpleGrantedAuthority(user.role.name)),
        )
    }
}
