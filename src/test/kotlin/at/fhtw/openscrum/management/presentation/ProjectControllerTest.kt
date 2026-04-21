package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.project.ProjectEntityRepository
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class ProjectControllerTest {
    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

    @Autowired
    lateinit var projectEntityRepository: ProjectEntityRepository

    @BeforeEach
    fun cleanUp() {
        projectEntityRepository.deleteAll()
        userEntityRepository.deleteAll()
        userService.registerAdmin()
    }

    /*
    Given a manager, a project name, a product owner, a scrum master and developers
    When the manager enters the information into the create project form
    Then he wants to create a project and see it in the projects list
     */
    @Test
    fun ensureCreateProjectWorksProperly() {
        // Given
        val projectName = "OpenScrum"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "product.owner",
            firstName = "Product",
            lastName = "Owner",
            password = "abc123",
            email = "product.owner@gmail.com",
        )
        userService.registerUser(
            authenticatedUser = admin,
            username = "scrum.master",
            firstName = "Scrum",
            lastName = "Master",
            password = "abc123",
            email = "scrum.master@gmail.com",
        )
        userService.registerUser(
            authenticatedUser = admin,
            username = "developer",
            firstName = "Developer",
            lastName = "User",
            password = "abc123",
            email = "developer@gmail.com",
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

        // fill in create project form
        webDriver.get("http://localhost:8080/projects/create")
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys(projectName)
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText("Product Owner")
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText("Scrum Master")
        Select(webDriver.findElement(By.cssSelector("select#developers"))).selectByVisibleText("Developer User")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".projects-list-item")))
        val pageSource = webDriver.pageSource
        assertThat(webDriver.currentUrl).isEqualTo("http://localhost:8080/projects")
        assertThat(pageSource).contains(projectName)
        webDriver.close()
    }

    /*
    Given a user, a project name, a product owner, a scrum master and developers
    When the user enters the information into the create project form
    Then he receives an error that he does not have the required permission
     */
    @Test
    fun ensureCreateProjectDoesNotWorkWithUserPermissions() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = username,
            firstName = "john",
            lastName = "doe",
            password = password,
            email = "user@gmail.com",
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
    Given a manager, a project name, a scrum master and developers
    When the manager enters the information into the create project form
    Then he receives an error that the product owner is missing
     */
    @Test
    fun ensureCreateProjectDoesNotWorkWithMissingProductOwner() {
        // Given
        val projectName = "OpenScrum"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "scrum.master",
            firstName = "Scrum",
            lastName = "Master",
            password = "abc123",
            email = "scrum.master@gmail.com",
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

        // fill in create project form without product owner
        webDriver.get("http://localhost:8080/projects/create")
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys(projectName)
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText("Scrum Master")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("product owner")
        webDriver.close()
    }

    /*
    Given a manager, a project name, a product owner and developers
    When the manager enters the information into the create project form
    Then he receives an error that the scrum master is missing
     */
    @Test
    fun ensureCreateProjectDoesNotWorkWithMissingScrumMaster() {
        // Given
        val projectName = "OpenScrum"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "product.owner",
            firstName = "Product",
            lastName = "Owner",
            password = "abc123",
            email = "product.owner@gmail.com",
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

        // fill in create project form without scrum master
        webDriver.get("http://localhost:8080/projects/create")
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys(projectName)
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText("Product Owner")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("scrum master")
        webDriver.close()
    }

    /*
    Given a manager, an already taken project name, a product owner, a scrum master and developers
    When the manager enters the information into the create project form
    Then he receives an error that the project name is already taken
     */
    @Test
    fun ensureCreateProjectDoesNotWorkWithTakenProjectName() {
        // Given
        val projectName = "OpenScrum"
        val productOwnerUsername = "product.owner"
        val scrumMasterUsername = "scrum.master"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = productOwnerUsername,
            firstName = "Product",
            lastName = "Owner",
            password = "abc123",
            email = "product.owner@gmail.com",
        )
        userService.registerUser(
            authenticatedUser = admin,
            username = scrumMasterUsername,
            firstName = "Scrum",
            lastName = "Master",
            password = "abc123",
            email = "scrum.master@gmail.com",
        )

        val productOwner = userEntityRepository.findByUsername(productOwnerUsername)!!.toUser()
        val scrumMaster = userEntityRepository.findByUsername(scrumMasterUsername)!!.toUser()

        projectService.createProject(
            authenticatedUser = admin,
            projectName = projectName,
            productOwner = productOwner,
            scrumMaster = scrumMaster,
            developers = setOf(),
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

        // fill in create project form with already taken project name
        webDriver.get("http://localhost:8080/projects/create")
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys(projectName)
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText(productOwner.fullName.fullName)
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText(scrumMaster.fullName.fullName)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase(projectName)
        webDriver.close()
    }

    /*
    Given a manager, a blank project name, a product owner, a scrum master and developers
    When the manager enters the information into the create project form
    Then he receives an error that the information is invalid
     */
    @Test
    fun ensureCreateProjectDoesNotWorkWithBlankProjectName() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "product.owner",
            firstName = "Product",
            lastName = "Owner",
            password = "abc123",
            email = "product.owner@gmail.com",
        )
        userService.registerUser(
            authenticatedUser = admin,
            username = "scrum.master",
            firstName = "Scrum",
            lastName = "Master",
            password = "abc123",
            email = "scrum.master@gmail.com",
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

        // fill in create project form with blank project name
        webDriver.get("http://localhost:8080/projects/create")
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText("Product Owner")
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText("Scrum Master")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("project name")
        webDriver.close()
    }

    /*
    Given a manager, a project name, a user as product owner, scrum master and developer
    When the manager enters the information into the create project form
    Then he receives an error one user can not have multiple roles
     */
    @Test
    fun ensureCreateProjectDoesNotWorkWhenOneUserHasMultipleRoles() {
        // Given
        val projectName = "OpenScrum"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        userService.registerUser(
            authenticatedUser = admin,
            username = "User",
            firstName = "Regular",
            lastName = "User",
            password = "abc123",
            email = "user@gmail.com",
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

        // fill in create project form with the same user in all roles
        webDriver.get("http://localhost:8080/projects/create")
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys(projectName)
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText("Regular User")
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText("Regular User")
        Select(webDriver.findElement(By.cssSelector("select#developers"))).selectByVisibleText("Regular User")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("multiple roles")
        webDriver.close()
    }
}
