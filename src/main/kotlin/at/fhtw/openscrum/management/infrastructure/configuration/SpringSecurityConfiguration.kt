package at.fhtw.openscrum.management.infrastructure.configuration

import io.github.wimdeblauwe.htmx.spring.boot.security.HxRefreshHeaderAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher

@Configuration
@EnableWebSecurity
class SpringSecurityConfiguration {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/css/**", permitAll)
                authorize("/assets/**", permitAll)
                authorize("/js/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
                loginPage = "/login"
                defaultSuccessUrl("/projects", true)
                permitAll()
            }
            logout {
                logoutUrl = "/logout"
                logoutSuccessHandler =
                    LogoutSuccessHandler { request, response, authentication ->
                        response.setHeader("HX-Redirect", "/login")
                    }
                permitAll()
            }
            exceptionHandling {
                defaultAuthenticationEntryPointFor(
                    HxRefreshHeaderAuthenticationEntryPoint(),
                    RequestHeaderRequestMatcher("HX-Request"),
                )
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
