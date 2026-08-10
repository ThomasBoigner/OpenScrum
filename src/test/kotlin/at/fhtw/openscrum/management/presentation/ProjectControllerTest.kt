package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.createHeadlessChromeDriver
import at.fhtw.openscrum.management.domain.model.project.ProjectId
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
        val developer =
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
        wait
            .until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='developerIds'][value='${developer.userId.token}']"),
                ),
            ).click()
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

        val user =
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
        wait
            .until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='developerIds'][value='${user.userId.token}']"),
                ),
            ).click()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("multiple roles")
        webDriver.close()
    }

    /*
    Given a manager, an existing project, a new project name, a new product owner, a new scrum master and new developers
    When the manager enters the information into the update project form
    Then the project information should be updated and ProductOwnerUnassigned, ProductOwnerAssigned, ScrumMasterUnassigned
    ScrumMasterAssigned, DeveloperUnassigned, DeveloperAssigned events should be published
     */
    @Test
    fun ensureUpdateProjectWorksProperly() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )
        val developer =
            userService.registerUser(
                authenticatedUser = admin,
                username = "developer",
                firstName = "Developer",
                lastName = "User",
                password = "abc123",
                email = "developer@gmail.com",
            )
        val newProductOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "new.product.owner",
                firstName = "New",
                lastName = "Owner",
                password = "abc123",
                email = "new.product.owner@gmail.com",
            )
        val newScrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "new.scrum.master",
                firstName = "New",
                lastName = "Master",
                password = "abc123",
                email = "new.scrum.master@gmail.com",
            )
        val newDeveloper =
            userService.registerUser(
                authenticatedUser = admin,
                username = "new.developer",
                firstName = "New",
                lastName = "Developer",
                password = "abc123",
                email = "new.developer@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
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

        // fill in update project form
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")
        webDriver.findElement(By.cssSelector("input#project-name")).clear()
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys("OpenScrum 2")
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText("New Owner")
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText("New Master")
        wait
            .until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='developerIds'][value='${developer.userId.token}']"),
                ),
            ).click()
        wait
            .until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='developerIds'][value='${newDeveloper.userId.token}']"),
                ),
            ).click()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".projects-list-item")))
        assertThat(webDriver.currentUrl).isEqualTo("http://localhost:8080/projects")
        assertThat(webDriver.pageSource).contains("OpenScrum 2")

        val updatedProject = projectEntityRepository.findByProjectId(project.projectId.token)!!
        assertThat(updatedProject.projectName).isEqualTo("OpenScrum 2")
        assertThat(updatedProject.productOwnerId).isEqualTo(newProductOwner.userId.token)
        assertThat(updatedProject.scrumMasterId).isEqualTo(newScrumMaster.userId.token)
        assertThat(updatedProject.developerIds).containsExactly(newDeveloper.userId.token)
        webDriver.close()
    }

    /*
    Given a user, an existing project, a new project name, a new product owner, a new scrum master and new developers
    When the user enters the information into the update project form
    Then he receives an error that he does not have the required permission
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWithUserPermissions() {
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
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a manager, no existing project, a new project name, a new product owner, a new scrum master and new developers
    When the user enters the information into the update project form
    Then he receives an error that the project does not exist
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWhenProjectDoesNotExist() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )

        val projectId = ProjectId()

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        // login as admin
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(admin.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(admin.username)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // open the update project form
        webDriver.get("http://localhost:8080/projects/${projectId.token}/update")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a manager, an existing project, a new project name, a new scrum master and new developers
    When the manager enters the information into the update project form
    Then he receives an error that the product owner is missing
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWithMissingProductOwner() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
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

        // fill in update project form without product owner
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByIndex(0)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("product owner")
        webDriver.close()
    }

    /*
    Given a manager, an existing project, a new project name, a new product owner and new developers
    When the manager enters the information into the update project form
    Then he receives an error that the scrum master is missing
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWithMissingScrumMaster() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
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

        // fill in update project form without scrum master
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByIndex(0)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("scrum master")
        webDriver.close()
    }

    /*
    Given a manager, an existing project, a new already taken project name, a new product owner, a new scrum master and new developers
    When the manager enters the information into the update project form
    Then he receives an error that the project name is already taken
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWithTakenProjectName() {
        // Given
        val takenProjectName = "Taken name"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )
        projectService.createProject(
            authenticatedUser = admin,
            projectName = takenProjectName,
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

        // fill in update project form with already taken project name
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")
        webDriver.findElement(By.cssSelector("input#project-name")).clear()
        webDriver.findElement(By.cssSelector("input#project-name")).sendKeys(takenProjectName)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase(takenProjectName)
        webDriver.close()
    }

    /*
    Given a manager, an existing project, a blank project name, a new product owner, a new scrum master and new developers
    When the manager enters the information into the update project form
    Then he receives an error that the project name can not be blank
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWithBlankProjectName() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
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

        // fill in update project form with blank project name
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")
        webDriver.findElement(By.cssSelector("input#project-name")).clear()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("project name")
        webDriver.close()
    }

    /*
    Given a manager, an existing project, a new project name, a user as the new product owner, the new scrum master and a new developer
    When the manager enters the information into the update project form
    Then he receives an error one user can not have multiple roles
     */
    @Test
    fun ensureUpdateProjectDoesNotWorkWhenOneUserHasMultipleRoles() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = "abc123",
                email = "scrum.master@gmail.com",
            )
        val user =
            userService.registerUser(
                authenticatedUser = admin,
                username = "User",
                firstName = "Regular",
                lastName = "User",
                password = "abc123",
                email = "user@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
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

        // fill in update project form with the same user in all roles
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/update")
        Select(webDriver.findElement(By.cssSelector("select#product-owner"))).selectByVisibleText("Regular User")
        Select(webDriver.findElement(By.cssSelector("select#scrum-master"))).selectByVisibleText("Regular User")
        wait
            .until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='developerIds'][value='${user.userId.token}']"),
                ),
            ).click()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#project-form button"))).click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("multiple roles")
        webDriver.close()
    }
}
