package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.createHeadlessChromeDriver
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.project.ProjectEntityRepository
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.command.MoveSprintBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.PlanSprintCommand
import at.fhtw.openscrum.scrum.domain.model.sprint.MoveDirection
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem.ProductBacklogItemEntityRepository
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint.SprintEntityRepository
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class SprintControllerKanbanBoardTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var sprintApplicationService: SprintApplicationService

    @Autowired
    lateinit var productBacklogItemApplicationService: ProductBacklogItemApplicationService

    @Autowired
    lateinit var teamMemberApplicationService: TeamMemberApplicationService

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
    lateinit var sprintEntityRepository: SprintEntityRepository

    @Autowired
    lateinit var productBacklogItemEntityRepository: ProductBacklogItemEntityRepository

    @BeforeEach
    fun cleanUp() {
        sprintEntityRepository.deleteAll()
        productBacklogItemEntityRepository.deleteAll()
        teamMemberEntityRepository.deleteAll()
        scrumProjectEntityRepository.deleteAll()
        managementProjectEntityRepository.deleteAll()
        userEntityRepository.deleteAll()
        userService.registerAdmin()
    }

    /*
    Given a developer, a sprint with status "in progress" and a sprint backlog item with status "To-Do"
    When the sprint backlog item gets moved right
    Then the status should be "In progress" and the developer should be assigned
     */
    @Test
    fun ensureMoveSprintBacklogItemRightWorksProperly() {
        // Given
        val itemTitle = "Implement Login"
        val itemDescription = "As a user, I want to log in to the application."

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

        val developerPassword = "abcdef123456"
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
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getTeamMemberOfProject(
                        project.projectId.token,
                        developer.username,
                    ) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = itemTitle,
                    description = itemDescription,
                ),
            )

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
                ),
            )

        sprintApplicationService.planSprint(
            scrumMaster.username,
            PlanSprintCommand(
                sprintGoal = "Deliver the login feature",
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogIds = setOf(productBacklogItem.productBacklogItemId),
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/kanban-board")
        val moveRightButton =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#item-${productBacklogItem.productBacklogItemId} .move-right-button"),
                ),
            )
        moveRightButton.click()

        // Then
        val inProgressItem =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#in-progress-column #item-${productBacklogItem.productBacklogItemId}"),
                ),
            )
        assertThat(inProgressItem.text).contains("${developer.fullName.firstName} ${developer.fullName.lastName}")

        webDriver.close()
    }

    /*
    Given a developer, a sprint with status "in progress" and a sprint backlog item with status "In progress"
    When the sprint backlog item gets moved right
    Then the status should be "Done" and the developer should be assigned
     */
    @Test
    fun ensureMoveSprintBacklogItemRightFromInProgressWorksProperly() {
        // Given
        val itemTitle = "Implement Login"
        val itemDescription = "As a user, I want to log in to the application."

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

        val developerPassword = "abcdef123456"
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
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getTeamMemberOfProject(
                        project.projectId.token,
                        developer.username,
                    ) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = itemTitle,
                    description = itemDescription,
                ),
            )

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
                ),
            )

        sprintApplicationService.planSprint(
            scrumMaster.username,
            PlanSprintCommand(
                sprintGoal = "Deliver the login feature",
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogIds = setOf(productBacklogItem.productBacklogItemId),
            ),
        )

        sprintApplicationService.moveSprintBacklogItem(
            developer.username,
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/kanban-board")
        val moveRightButton =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#in-progress-column #item-${productBacklogItem.productBacklogItemId} .move-right-button"),
                ),
            )
        moveRightButton.click()

        // Then
        val doneItem =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#done-column #item-${productBacklogItem.productBacklogItemId}"),
                ),
            )
        assertThat(doneItem.text).contains("${developer.fullName.firstName} ${developer.fullName.lastName}")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))

        val productBacklogPageSource = webDriver.pageSource
        assertThat(productBacklogPageSource).contains("Committed to sprint (done)")

        webDriver.close()
    }

    /*
    Given a developer, a sprint with status "in progress" and a sprint backlog item with status "Done"
    When the sprint backlog item gets moved left
    Then the status should be "In progress" and the developer should be assigned
     */
    @Test
    fun ensureMoveSprintBacklogItemLeftFromDoneWorksProperly() {
        // Given
        val itemTitle = "Implement Login"
        val itemDescription = "As a user, I want to log in to the application."

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

        val developerPassword = "abcdef123456"
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
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getTeamMemberOfProject(
                        project.projectId.token,
                        developer.username,
                    ) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = itemTitle,
                    description = itemDescription,
                ),
            )

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
                ),
            )

        sprintApplicationService.planSprint(
            scrumMaster.username,
            PlanSprintCommand(
                sprintGoal = "Deliver the login feature",
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogIds = setOf(productBacklogItem.productBacklogItemId),
            ),
        )

        val moveCommand =
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
            )
        sprintApplicationService.moveSprintBacklogItem(developer.username, moveCommand)
        sprintApplicationService.moveSprintBacklogItem(developer.username, moveCommand)

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(developer.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(developerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/kanban-board")
        val moveLeftButton =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#done-column #item-${productBacklogItem.productBacklogItemId} .move-left-button"),
                ),
            )
        moveLeftButton.click()

        // Then
        val inProgressItem =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#in-progress-column #item-${productBacklogItem.productBacklogItemId}"),
                ),
            )
        assertThat(inProgressItem.text).contains("${developer.fullName.firstName} ${developer.fullName.lastName}")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))

        val productBacklogPageSource = webDriver.pageSource
        assertThat(productBacklogPageSource).contains("Committed to sprint")

        webDriver.close()
    }

    /*
    Given a developer, a sprint with status "in progress" and a sprint backlog item with status "In progress"
    When the sprint backlog item gets moved left
    Then the status should be "To-Do" and no developer should be assigned
     */
    @Test
    fun ensureMoveSprintBacklogItemLeftFromInProgressWorksProperly() {
        // Given
        val itemTitle = "Implement Login"
        val itemDescription = "As a user, I want to log in to the application."

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

        val developerPassword = "abcdef123456"
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
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getTeamMemberOfProject(
                        project.projectId.token,
                        developer.username,
                    ) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = itemTitle,
                    description = itemDescription,
                ),
            )

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
                ),
            )

        sprintApplicationService.planSprint(
            scrumMaster.username,
            PlanSprintCommand(
                sprintGoal = "Deliver the login feature",
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogIds = setOf(productBacklogItem.productBacklogItemId),
            ),
        )

        sprintApplicationService.moveSprintBacklogItem(
            developer.username,
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/kanban-board")
        val moveLeftButton =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#in-progress-column #item-${productBacklogItem.productBacklogItemId} .move-left-button"),
                ),
            )
        moveLeftButton.click()

        // Then
        val todoItem =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#todo-column #item-${productBacklogItem.productBacklogItemId}"),
                ),
            )
        assertThat(todoItem.text).doesNotContain("${developer.fullName.firstName} ${developer.fullName.lastName}")

        webDriver.close()
    }

    /*
    Given a developer of another project, a sprint with status "in progress" and a sprint backlog item
    When the sprint backlog item gets moved
    Then he receives an error that he is not a developer of this project
     */
    @Test
    fun ensureMoveSprintBacklogItemDoesNotWorkWhenUserIsNotDeveloperOfThisProject() {
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = "Implement Login",
                    description = "As a user, I want to log in to the application.",
                ),
            )

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
                ),
            )

        sprintApplicationService.planSprint(
            scrumMaster.username,
            PlanSprintCommand(
                sprintGoal = "Deliver the login feature",
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogIds = setOf(productBacklogItem.productBacklogItemId),
            ),
        )

        val otherDeveloperPassword = "abcdef123456"
        val otherDeveloper =
            userService.registerUser(
                authenticatedUser = admin,
                username = "developer.other",
                firstName = "Other",
                lastName = "Developer",
                password = otherDeveloperPassword,
                email = "developer.other@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(otherDeveloper.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(otherDeveloperPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/kanban-board")

        // Then
        assertThat(webDriver.pageSource).contains("404")

        webDriver.close()
    }
}
