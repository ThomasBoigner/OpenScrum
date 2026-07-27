package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.createHeadlessChromeDriver
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.project.ProjectEntityRepository
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem.ProductBacklogItemEntityRepository
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember.TeamMemberEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class ProductBacklogControllerTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

    @Autowired
    @Qualifier("managementProjectEntityRepository")
    lateinit var managementProjectEntityRepository: ProjectEntityRepository

    @Autowired
    @Qualifier("scrumProjectEntityRepository")
    lateinit var scrumProjectEntityRepository: at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project.ProjectEntityRepository

    @Autowired
    lateinit var teamMemberEntityRepository: TeamMemberEntityRepository

    @Autowired
    lateinit var productBacklogItemEntityRepository: ProductBacklogItemEntityRepository

    @Autowired
    lateinit var productBacklogItemApplicationService: ProductBacklogItemApplicationService

    @Autowired
    lateinit var teamMemberApplicationService: TeamMemberApplicationService

    @BeforeEach
    fun cleanUp() {
        productBacklogItemEntityRepository.deleteAll()
        teamMemberEntityRepository.deleteAll()
        scrumProjectEntityRepository.deleteAll()
        managementProjectEntityRepository.deleteAll()
        userEntityRepository.deleteAll()
        userService.registerAdmin()
    }

    /*
    Given a product owner, a title and a description
    When the product owner enters the information into the define product backlog item form
    Then the product backlog item should be created
     */
    @Test
    fun ensureDefineProductBacklogItemWorksProperly() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/define")
        webDriver.findElement(By.cssSelector("input#title")).sendKeys(title)
        webDriver.findElement(By.cssSelector("textarea#description")).sendKeys(description)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#define-product-backlog-item-form button")))
            .click()
        wait.until(ExpectedConditions.urlContains("/backlog"))

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(title)
        assertThat(pageSource).contains(description)
        webDriver.close()
    }

    /*
    Given a product owner of another project, a title and a description
    When the product owner enters the information into the define product backlog item form
    Then he receives an error that he is not the product owner of this project
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenUserIsNotProductOwnerOfThisProject() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner1 =
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
                productOwner = productOwner1,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )

        val productOwner2Password = "abc123"
        val productOwner2 =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner.other",
                firstName = "Other",
                lastName = "Owner",
                password = productOwner2Password,
                email = "product.owner.other@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner2.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwner2Password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/define")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a developer, a title and a description
    When the developer enters the information into the define product backlog item form
    Then he receives an error that he has no permission to create a product backlog item
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenUserIsDeveloper() {
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

        val developerPassword = "abc123"
        val developer =
            userService.registerUser(
                authenticatedUser = admin,
                username = "developer",
                firstName = "Developer",
                lastName = "Developer",
                password = developerPassword,
                email = "developer@gmail.com",
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
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(developer.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(developerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/define")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a scrum master, a title and a description
    When the scrum master enters the information into the define product backlog item form
    Then he receives an error that he has no permission to create a product backlog item
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenUserIsScrumMaster() {
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

        val scrumMasterPassword = "abc123"
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = scrumMasterPassword,
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
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/define")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a product owner, a blank title and a description
    When the product owner enters the information into the define product backlog item form
    Then he should receive an error that the title must not be blank
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenTitleIsBlank() {
        // Given
        val description = "The login page should allow users to sign in with their credentials"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/define")
        webDriver.findElement(By.cssSelector("textarea#description")).sendKeys(description)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#define-product-backlog-item-form button")))
            .click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error.text).containsIgnoringCase("title")
        webDriver.close()
    }

    /*
    Given a product owner, a title and a blank description
    When the product owner enters the information into the define product backlog item form
    Then he should receive an error that the description must not be blank
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenDescriptionIsBlank() {
        // Given
        val title = "Implement login page"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/define")
        webDriver.findElement(By.cssSelector("input#title")).sendKeys(title)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#define-product-backlog-item-form button")))
            .click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error.text).containsIgnoringCase("description")
        webDriver.close()
    }

    /*
    Given a product owner, a product backlog item, a title and a description
    When the product owner enters the information into the update product backlog item form of the item
    Then the product backlog item should be updated
     */
    @Test
    fun ensureUpdateProductBacklogItemWorksProperly() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"
        val updatedTitle = "Implement login and registration page"
        val updatedDescription = "The login and registration page should allow users to sign in and sign up"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                authenticatedUserUsername = productOwner.username,
                command =
                    DefineProductBacklogItemCommand(
                        projectId = project.projectId.token,
                        title = title,
                        description = description,
                    ),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get(
            "http://localhost:8080/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )
        val titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#title")))
        titleField.clear()
        titleField.sendKeys(updatedTitle)
        val descriptionField = webDriver.findElement(By.cssSelector("textarea#description"))
        descriptionField.clear()
        descriptionField.sendKeys(updatedDescription)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-product-backlog-item-form button")))
            .click()
        wait.until(ExpectedConditions.urlContains("/backlog"))

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(updatedTitle)
        assertThat(pageSource).contains(updatedDescription)
        webDriver.close()
    }

    /*
    Given a product owner of another project, a product backlog item, a title and a description
    When the product owner enters the information into the update product backlog item form of the item
    Then he receives an error that he is not the product owner of this project
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenUserIsNotProductOwnerOfThisProject() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwner1 =
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
                productOwner = productOwner1,
                scrumMaster = scrumMaster,
                developers = setOf(),
            )

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                authenticatedUserUsername = productOwner1.username,
                command =
                    DefineProductBacklogItemCommand(
                        projectId = project.projectId.token,
                        title = title,
                        description = description,
                    ),
            )

        val productOwner2Password = "abc123"
        val productOwner2 =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner.other",
                firstName = "Other",
                lastName = "Owner",
                password = productOwner2Password,
                email = "product.owner.other@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner2.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwner2Password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get(
            "http://localhost:8080/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a developer, a product backlog item, a title and a description
    When the developer enters the information into the update product backlog item form of the item
    Then he receives an error that he has no permission to update a product backlog item
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenUserIsDeveloper() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"

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

        val developerPassword = "abc123"
        val developer =
            userService.registerUser(
                authenticatedUser = admin,
                username = "developer",
                firstName = "Developer",
                lastName = "Developer",
                password = developerPassword,
                email = "developer@gmail.com",
            )

        val project =
            projectService.createProject(
                authenticatedUser = admin,
                projectName = "OpenScrum",
                productOwner = productOwner,
                scrumMaster = scrumMaster,
                developers = setOf(developer),
            )

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                authenticatedUserUsername = productOwner.username,
                command =
                    DefineProductBacklogItemCommand(
                        projectId = project.projectId.token,
                        title = title,
                        description = description,
                    ),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(developer.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(developerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get(
            "http://localhost:8080/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a scrum master, a product backlog item, a title and a description
    When the scrum master enters the information into the update product backlog item form of the item
    Then he receives an error that he has no permission to update a product backlog item
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenUserIsScrumMaster() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"

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

        val scrumMasterPassword = "abc123"
        val scrumMaster =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master",
                firstName = "Scrum",
                lastName = "Master",
                password = scrumMasterPassword,
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                authenticatedUserUsername = productOwner.username,
                command =
                    DefineProductBacklogItemCommand(
                        projectId = project.projectId.token,
                        title = title,
                        description = description,
                    ),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get(
            "http://localhost:8080/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a product owner, no backlog item, a title and a description
    When the product owner enters the information into the update product backlog item form of the item
    Then he receives an error that the product backlog item does not exist
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenItemDoesNotExist() {
        // Given
        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog/${UUID.randomUUID()}/update")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a product owner, a product backlog item, a blank title and a description
    When the product owner enters the information into the update product backlog item form of the item
    Then he receives an error that the title must not be blank
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenTitleIsBlank() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"
        val updatedDescription = "The login and registration page should allow users to sign in and sign up"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                authenticatedUserUsername = productOwner.username,
                command =
                    DefineProductBacklogItemCommand(
                        projectId = project.projectId.token,
                        title = title,
                        description = description,
                    ),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get(
            "http://localhost:8080/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )
        val titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#title")))
        titleField.clear()
        val descriptionField = webDriver.findElement(By.cssSelector("textarea#description"))
        descriptionField.clear()
        descriptionField.sendKeys(updatedDescription)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-product-backlog-item-form button")))
            .click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error.text).containsIgnoringCase("title")
        webDriver.close()
    }

    /*
    Given a product owner, a product backlog item, a title and a blank description
    When the product owner enters the information into the update product backlog item form of the item
    Then he receives an error that the description must not be blank
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenDescriptionIsBlank() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"
        val updatedTitle = "Implement login and registration page"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                authenticatedUserUsername = productOwner.username,
                command =
                    DefineProductBacklogItemCommand(
                        projectId = project.projectId.token,
                        title = title,
                        description = description,
                    ),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get(
            "http://localhost:8080/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )
        val titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#title")))
        titleField.clear()
        titleField.sendKeys(updatedTitle)
        val descriptionField = webDriver.findElement(By.cssSelector("textarea#description"))
        descriptionField.clear()
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#update-product-backlog-item-form button")))
            .click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error.text).containsIgnoringCase("description")
        webDriver.close()
    }

    /*
    Given a product owner and a product backlog item that is not committed to a sprint
    When the product owner clicks the delete product backlog item button
    Then the product backlog item should be deleted
     */
    @Test
    fun ensureDeleteProductBacklogItemWorksProperly() {
        // Given
        val title = "Implement login page"
        val description = "The login page should allow users to sign in with their credentials"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerPassword = "abc123"
        val productOwner =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = productOwnerPassword,
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        productBacklogItemApplicationService.defineProductBacklogItem(
            authenticatedUserUsername = productOwner.username,
            command =
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = title,
                    description = description,
                ),
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog")
        val productBacklogListItem =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".product-backlog-list-item .delete-button")))
            .click()

        // Then
        wait.until(ExpectedConditions.stalenessOf(productBacklogListItem))
        assertThat(webDriver.findElements(By.cssSelector(".product-backlog-list-item"))).isEmpty()
        webDriver.close()
    }
}
