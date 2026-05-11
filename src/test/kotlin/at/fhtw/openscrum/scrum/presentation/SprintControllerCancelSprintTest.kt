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
import org.springframework.modulith.moments.support.TimeMachine
import org.springframework.test.context.ActiveProfiles
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("postgres")
class SprintControllerCancelSprintTest {
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

    @Autowired
    lateinit var timeMachine: TimeMachine

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
    Given a product owner and a sprint with status "in progress"
    When the product owner clicks the cancel sprint button
    Then the sprint status should be updated to canceled, not finished sprint backlog items should have status
    "in product backlog" and the next sprint should be scheduled
     */
    @Test
    fun ensureCancelInProgressSprintWorksProperly() {
        // Given
        val itemTitle = "Implement Login"
        val itemDescription = "As a user, I want to log in to the application."

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

        val developer =
            userService.registerUser(
                authenticatedUser = admin,
                username = "developer",
                firstName = "Developer",
                lastName = "Developer",
                password = "abc123",
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

        val productBacklogItem1 =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = itemTitle,
                    description = itemDescription,
                ),
            )

        val productBacklogItem2 =
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
                productBacklogIds =
                    setOf(
                        productBacklogItem1.productBacklogItemId,
                        productBacklogItem2.productBacklogItemId,
                    ),
            ),
        )

        sprintApplicationService.moveSprintBacklogItem(
            developer.username,
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem1.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
            ),
        )

        sprintApplicationService.moveSprintBacklogItem(
            developer.username,
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem1.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.cancel-sprint-button"))).click()

        // Then
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#sprint-details"), "Cancelled"))
        assertThat(webDriver.pageSource).contains("Cancelled")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.sprint-list-item")))

        assertThat(webDriver.pageSource).contains("Sprint 2")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))

        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("In backlog")
        assertThat(pageSource).contains("Done")

        webDriver.close()
    }

    @Test
    fun ensureCompleteInProgressSprintWorksProperly() {
        // Given
        val itemTitle = "Implement Login"
        val itemDescription = "As a user, I want to log in to the application."

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

        val developer =
            userService.registerUser(
                authenticatedUser = admin,
                username = "developer",
                firstName = "Developer",
                lastName = "Developer",
                password = "abc123",
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

        val productBacklogItem1 =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = itemTitle,
                    description = itemDescription,
                ),
            )

        val productBacklogItem2 =
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
                productBacklogIds =
                    setOf(
                        productBacklogItem1.productBacklogItemId,
                        productBacklogItem2.productBacklogItemId,
                    ),
            ),
        )

        sprintApplicationService.moveSprintBacklogItem(
            developer.username,
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem1.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
            ),
        )

        sprintApplicationService.moveSprintBacklogItem(
            developer.username,
            MoveSprintBacklogItemCommand(
                projectId = project.projectId.token,
                sprintId = sprint.sprintId,
                productBacklogItemId = productBacklogItem1.productBacklogItemId,
                moveDirection = MoveDirection.RIGHT,
            ),
        )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        timeMachine.shiftBy(
            Duration.between(
                LocalDateTime.now(),
                LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).plusWeeks(2),
            ),
        )

        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        // Then
        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}")
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#sprint-details"), "Completed"))
        assertThat(webDriver.pageSource).contains("Completed")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.sprint-list-item")))

        assertThat(webDriver.pageSource).contains("Sprint 2")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))

        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("In backlog")
        assertThat(pageSource).contains("Done")

        webDriver.close()
    }
}
