package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class AuthenticationControllerTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

    @AfterEach
    fun cleanUp() {
        userEntityRepository.deleteAll()
    }

    /*
    Given an email and a password
    When I enter the email and password
    Then I want to be logged in
     */
    @Test
    fun ensureLoginWorksProperly() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        userService.registerUser(
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
            role = Role.USER,
        )

        val webDriver = ChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        webDriver.findElement(By.cssSelector("section#login-form button")).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // Then
        assertThat(webDriver.currentUrl).isEqualTo("http://localhost:8080/projects")
        webDriver.close()
    }

    /*
    Given an email and a password
    When I enter the wrong email
    Then I receive an error
     */
    @Test
    fun ensureLoginFailsWithWrongEmail() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        userService.registerUser(
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
            role = Role.USER,
        )

        val webDriver = ChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("Wrong Username")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        webDriver.findElement(By.cssSelector("section#login-form button")).click()

        // Then
        assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))).isNotNull
        webDriver.close()
    }

    /*
    Given an email and a password
    When I enter the wrong password
    Then I receive an error
     */
    @Test
    fun ensureLoginFailsWithWrongPassword() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        userService.registerUser(
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
            role = Role.USER,
        )

        val webDriver = ChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("Wrong Password")
        webDriver.findElement(By.cssSelector("section#login-form button")).click()

        // Then
        assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))).isNotNull
        webDriver.close()
    }
}
