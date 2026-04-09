package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class UserControllerTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

    @AfterEach
    fun cleanUp() {
        userEntityRepository.deleteAll()
        userService.registerAdmin()
    }

    /*
    Given a manager, a username, a first name, a last name, an email address and a password
    When the manager enters the information
    Then the manager wants to register a user and see them in the user list
     */
    @Test
    fun ensureRegisterUserWorksProperly() {
        // Given
        val username = "john.doe"
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@gmail.com"
        val password = "abc123"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#login-form button")))
        webDriver.findElement(By.cssSelector("section#login-form button")).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#registration-form button")))
        webDriver.findElement(By.cssSelector("section#registration-form button")).click()
        wait.until(ExpectedConditions.urlContains("/users"))

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".users-list-item")))
        val pageSource = webDriver.pageSource
        assertThat(webDriver.currentUrl).isEqualTo("http://localhost:8080/users")
        assertThat(pageSource).contains(username)
        assertThat(pageSource).contains(firstName)
        assertThat(pageSource).contains(lastName)
        assertThat(pageSource).contains(email)
        webDriver.close()
    }

    /*
    Given a user, a username, a first name, a last name, an email address and a password
    When the user enters the information
    Then he receives an error that he does not have the required permission
     */
    @Test
    fun ensureRegisterUserDoesNotWorkWithUserPermissions() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#login-form button")))
        webDriver.findElement(By.cssSelector("section#login-form button")).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/users/register")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a manager, an already taken username, a first name, a last name, an email address and a password
    When the manager enters the information
    Then he receives an error that the username is already taken
     */
    @Test
    fun ensureRegisterUserDoesNotWorkWithTakenUsername() {
        // Given
        val username = "john.doe"
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@gmail.com"
        val password = "abc123"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = username,
            firstName = firstName,
            lastName = lastName,
            password = password,
            email = "john.doe2@gmail.com",
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#login-form button")))
        webDriver.findElement(By.cssSelector("section#login-form button")).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#registration-form button")))
        webDriver.findElement(By.cssSelector("section#registration-form button")).click()
        wait.until(ExpectedConditions.urlContains("/users"))

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("username")
        webDriver.close()
    }

    /*
    Given a manager, a username, a first name, a last name, an already taken email address and a password
    When the manager enters the information
    Then he receives an error that the email address is already taken
     */
    @Test
    fun ensureRegisterUserDoesNotWorkWithTakenEmailAddress() {
        // Given
        val username = "john.doe"
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@gmail.com"
        val password = "abc123"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "john.doe2",
            firstName = firstName,
            lastName = lastName,
            password = password,
            email = email,
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#login-form button")))
        webDriver.findElement(By.cssSelector("section#login-form button")).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#registration-form button")))
        webDriver.findElement(By.cssSelector("section#registration-form button")).click()
        wait.until(ExpectedConditions.urlContains("/users"))

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("email")
        webDriver.close()
    }

    /*
    Given a manager, a blank username, a blank first name, a blank last name, a blank email address and a blank password
    When the manager enters the information
    Then he receives an error that the information is invalid
     */
    @Test
    fun ensureRegisterUserDoesNotWorkWithInvalidInformation() {
        // Given
        val username = ""
        val firstName = ""
        val lastName = ""
        val email = ""
        val password = ""

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#login-form button")))
        webDriver.findElement(By.cssSelector("section#login-form button")).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section#registration-form button")))
        webDriver.findElement(By.cssSelector("section#registration-form button")).click()
        wait.until(ExpectedConditions.urlContains("/users"))

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("Username")
        assertThat(error.text).contains("Email address")
        assertThat(error.text).contains("First name")
        assertThat(error.text).contains("Last name")
        assertThat(error.text).contains("Password")
        webDriver.close()
    }
}
