package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.createHeadlessChromeDriver
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember.DeveloperEntity
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember.TeamMemberEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class UserControllerTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var teamMemberEntityRepository: TeamMemberEntityRepository

    @BeforeEach
    fun cleanUp() {
        teamMemberEntityRepository.deleteAll()
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
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#registration-form button"))).click()
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
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
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
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#registration-form button"))).click()
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
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#registration-form button"))).click()
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
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // register user
        webDriver.get("http://localhost:8080/users/register")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys(firstName)
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys(lastName)
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys(email)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#registration-form button"))).click()
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

    /*
    Given a manager and a user
    When the manager clicks the promote user button
    Then the user should have the role of manager
     */
    @Test
    fun ensurePromoteUserWorksProperly() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // promote user
        webDriver.get("http://localhost:8080/users")
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#user-${user.userId.token} .promote-button")),
        )
        // htmx attaches its listeners in the settle phase after the swap, so the click is dispatched via JavaScript
        // and retried until the promote button is removed with the re-rendered row.
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .until {
                webDriver.executeScript("document.querySelector('#user-${user.userId.token} .promote-button')?.click()")
                webDriver.findElements(By.cssSelector("#user-${user.userId.token} .promote-button")).isEmpty()
            }

        // Then
        assertThat(webDriver.findElements(By.cssSelector("#user-${user.userId.token}"))).hasSize(1)
        assertThat(webDriver.findElement(By.cssSelector("#user-${user.userId.token}")).text).contains("Manager")
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.role).isEqualTo(Role.MANAGER)
        webDriver.close()
    }

    /*
    Given a manager and a manager
    When the manager clicks on the demote user button
    Then the user should have the role of user
     */
    @Test
    fun ensureDemoteUserWorksProperly() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )
        // reload the persisted user so save performs an update instead of inserting a duplicate
        val persistedUser = userRepository.findByUserId(user.userId)!!
        persistedUser.promote(admin)
        userRepository.save(persistedUser)

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // demote user
        webDriver.get("http://localhost:8080/users")
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#user-${user.userId.token} .demote-button")),
        )
        // htmx attaches its listeners in the settle phase after the swap, so the click is dispatched via JavaScript
        // and retried until the demote button is removed with the re-rendered row.
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .until {
                webDriver.executeScript("document.querySelector('#user-${user.userId.token} .demote-button')?.click()")
                webDriver.findElements(By.cssSelector("#user-${user.userId.token} .demote-button")).isEmpty()
            }

        // Then
        assertThat(webDriver.findElements(By.cssSelector("#user-${user.userId.token}"))).hasSize(1)
        assertThat(webDriver.findElements(By.cssSelector("#user-${user.userId.token} .promote-button"))).hasSize(1)
        assertThat(webDriver.findElements(By.cssSelector("#user-${admin.userId.token} .demote-button"))).isEmpty()
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.role).isEqualTo(Role.USER)
        webDriver.close()
    }

    /*
    Given a manager and an existing user, a new username, a new first name, a new last name, a new email address and a new password
    When the manager enters the information into the update user form
    Then the user information should be updated and a UserInformationChanged event should be published
     */
    @Test
    fun ensureUpdateUserWorksProperly() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#first-name")).clear()
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys("Jane")
        webDriver.findElement(By.cssSelector("input#last-name")).clear()
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys("Doe")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("jane.doe@gmail.com")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/users"))

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".users-list-item")))
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("jane.doe")
        assertThat(pageSource).contains("Jane")
        assertThat(pageSource).contains("jane.doe@gmail.com")

        val updatedUser = userEntityRepository.findByUserId(user.userId.token)!!
        assertThat(updatedUser.username).isEqualTo("jane.doe")
        assertThat(updatedUser.emailAddress).isEqualTo("jane.doe@gmail.com")
        assertThat(updatedUser.fullName.firstName).isEqualTo("Jane")
        assertThat(updatedUser.fullName.lastName).isEqualTo("Doe")
        webDriver.close()
    }

    /*
    Given an existing user, a new username, a new first name, a new last name, a new email address and a new password
    When the user enters the information into the update user form of his own user
    Then the user information should be updated and a UserInformationChanged event should be published
     */
    @Test
    fun ensureUpdateUserWorksForOwnUser() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as the user
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("john.doe")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("abc123")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update own user
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#first-name")).clear()
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys("Jane")
        webDriver.findElement(By.cssSelector("input#last-name")).clear()
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys("Doe")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("jane.doe@gmail.com")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()

        // Then
        // updating the own account logs the user out
        wait.until(ExpectedConditions.urlContains("/login"))
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.username).isEqualTo("jane.doe")

        // login with the new credentials
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))
        webDriver.close()
    }

    /*
    Given an existing user, a new username, a new first name, a new last name, a new email address and a new password
    When the user enters the information into the update user form of another user
    Then he receives an error that he does not have the required permission to update other users
     */
    @Test
    fun ensureUpdateUserDoesNotWorkForOtherUsersWithUserPermissions() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "john.doe",
            firstName = "John",
            lastName = "Doe",
            password = "abc123",
            email = "john.doe@gmail.com",
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as the user
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("john.doe")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("abc123")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // open the update user page of the admin user
        webDriver.get("http://localhost:8080/users/${admin.userId.token}/update")

        // Then
        assertThat(webDriver.pageSource).contains("403")
        assertThat(webDriver.findElements(By.cssSelector("section#update-user-form"))).isEmpty()
        assertThat(userEntityRepository.findByUserId(admin.userId.token)!!.username).isEqualTo("admin")
        webDriver.close()
    }

    /*
    Given a manager and no existing user, a new username, a new first name, a new last name, a new email address and a new password
    When the manager enters the information into the update user form
    Then he receives an error that the user does not exist
     */
    @Test
    fun ensureUpdateUserDoesNotWorkForNonExistingUser() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()
        val nonExistingUserId = UUID.randomUUID()

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // open the update user page of a user that does not exist
        webDriver.get("http://localhost:8080/users/$nonExistingUserId/update")

        // Then
        assertThat(webDriver.pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, an already taken username, a new first name, a new last name, a new email address and a new password
    When the manager enters the information into the update user form
    Then he receives an error that the username is already taken
     */
    @Test
    fun ensureUpdateUserDoesNotWorkWithTakenUsername() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user with the taken username of the admin
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("admin")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("username")
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.username).isEqualTo("john.doe")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, a new username, a new first name, a new last name, an already taken email address and a new password
    When the manager enters the information into the update user form
    Then he receives an error that the email address is already taken
     */
    @Test
    fun ensureUpdateUserDoesNotWorkWithTakenEmailAddress() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user with the taken email address of the admin
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("admin@gmail.com")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("email")
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.emailAddress).isEqualTo("john.doe@gmail.com")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, the user's own current username, a new first name, a new last name, a new email address and a new password
    When the manager enters the information into the update user form
    Then the user information should be updated and a UserInformationChanged event should be published
     */
    @Test
    fun ensureUpdateUserWorksWithOwnCurrentUsername() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user keeping the current username
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("jane.doe@gmail.com")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/users"))

        // Then
        val updatedUser = userEntityRepository.findByUserId(user.userId.token)!!
        assertThat(updatedUser.username).isEqualTo("john.doe")
        assertThat(updatedUser.emailAddress).isEqualTo("jane.doe@gmail.com")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, a new username, a new first name, a new last name, the user's own current email address and a new password
    When the manager enters the information into the update user form
    Then the user information should be updated and a UserInformationChanged event should be published
     */
    @Test
    fun ensureUpdateUserWorksWithOwnCurrentEmailAddress() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user keeping the current email address
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/users"))

        // Then
        val updatedUser = userEntityRepository.findByUserId(user.userId.token)!!
        assertThat(updatedUser.username).isEqualTo("jane.doe")
        assertThat(updatedUser.emailAddress).isEqualTo("john.doe@gmail.com")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, a new username, a new first name, a new last name, a new email address that does not have the right format and a new password
    When the manager enters the information into the update user form
    Then he receives an error that the email address does not have the right format
     */
    @Test
    fun ensureUpdateUserDoesNotWorkWithInvalidEmailAddress() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user with an invalid email address
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("invalid-email")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("Email address must be valid!")
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.emailAddress).isEqualTo("john.doe@gmail.com")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, a new blank username, a new blank first name, a new blank last name and a new blank email address
    When the manager enters the information into the update user form
    Then he receives an error that the information is invalid
     */
    @Test
    fun ensureUpdateUserDoesNotWorkWithInvalidInformation() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user with blank information
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#first-name")).clear()
        webDriver.findElement(By.cssSelector("input#last-name")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#password")).clear()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).contains("Username")
        assertThat(error.text).contains("Email address")
        assertThat(error.text).contains("First name")
        assertThat(error.text).contains("Last name")
        assertThat(userEntityRepository.findByUserId(user.userId.token)!!.username).isEqualTo("john.doe")
        webDriver.close()
    }

    /*
    Given a manager and an existing user, a new username, a new first name, a new last name, a new email address and a blank password
    When the manager enters the information into the update user form
    Then the user information should be updated, the password should stay unchanged and a UserInformationChanged event should be published
     */
    @Test
    fun ensureUpdateUserKeepsPasswordWhenPasswordIsBlank() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )
        val passwordBeforeUpdate = userEntityRepository.findByUserId(user.userId.token)!!.password

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user leaving the password blank
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#first-name")).clear()
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys("Jane")
        webDriver.findElement(By.cssSelector("input#last-name")).clear()
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys("Doe")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("jane.doe@gmail.com")
        webDriver.findElement(By.cssSelector("input#password")).clear()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/users"))

        // Then
        val updatedUser = userEntityRepository.findByUserId(user.userId.token)!!
        assertThat(updatedUser.username).isEqualTo("jane.doe")
        assertThat(updatedUser.emailAddress).isEqualTo("jane.doe@gmail.com")
        assertThat(updatedUser.fullName.firstName).isEqualTo("Jane")
        assertThat(updatedUser.password).isEqualTo(passwordBeforeUpdate)

        // the old password still works
        webDriver.manage().deleteAllCookies()
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("abc123")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))
        webDriver.close()
    }

    /*
    Given a teammember and a UserInformationChanged event
    When the UserInformationChanged event is received
    Then the teammember information should be updated
     */
    @Test
    fun ensureUpdateUserUpdatesTeamMemberInformation() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        teamMemberEntityRepository.save(
            DeveloperEntity(
                Developer(
                    teamMemberId = TeamMemberId(userId = user.userId.token, projectId = UUID.randomUUID()),
                    username = user.username,
                    fullName = FullName(firstName = "John", lastName = "Doe"),
                ),
            ),
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // update user
        webDriver.get("http://localhost:8080/users/${user.userId.token}/update")
        webDriver.findElement(By.cssSelector("input#username")).clear()
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("jane.doe")
        webDriver.findElement(By.cssSelector("input#first-name")).clear()
        webDriver.findElement(By.cssSelector("input#first-name")).sendKeys("Jane")
        webDriver.findElement(By.cssSelector("input#last-name")).clear()
        webDriver.findElement(By.cssSelector("input#last-name")).sendKeys("Doe")
        webDriver.findElement(By.cssSelector("input#email-address")).clear()
        webDriver.findElement(By.cssSelector("input#email-address")).sendKeys("jane.doe@gmail.com")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("def456")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-user-form button"))).click()
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/users"))

        // Then
        // the UserInformationChanged event is processed asynchronously by the scrum context
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted {
                val teamMembers = teamMemberEntityRepository.findAllByUserId(user.userId.token)
                assertThat(teamMembers).hasSize(1)
                assertThat(teamMembers.first().username).isEqualTo("jane.doe")
                assertThat(teamMembers.first().firstName).isEqualTo("Jane")
                assertThat(teamMembers.first().lastName).isEqualTo("Doe")
            }
        webDriver.close()
    }

    /*
    Given a manager and a user that is not assigned to a project
    When the manager clicks the delete user button
    Then the user should be deleted
     */
    @Test
    fun ensureDeleteUserWorksProperly() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "john.doe",
                firstName = "John",
                lastName = "Doe",
                password = "abc123",
                email = "john.doe@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // delete user
        webDriver.get("http://localhost:8080/users")
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#user-${user.userId.token} .delete-button")),
        )
        // The floating create button overlaps the delete button, so the click is dispatched via JavaScript.
        // htmx attaches its listeners in the settle phase after the swap, so the click is retried until the row is removed.
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .until {
                webDriver.executeScript("document.querySelector('#user-${user.userId.token} .delete-button')?.click()")
                webDriver.findElements(By.cssSelector("#user-${user.userId.token}")).isEmpty()
            }

        // Then
        assertThat(webDriver.findElements(By.cssSelector("#user-${user.userId.token}"))).isEmpty()
        assertThat(webDriver.findElements(By.cssSelector(".users-list-item"))).hasSize(1)
        assertThat(userEntityRepository.findByUserId(user.userId.token)).isNull()
        webDriver.close()
    }
}
