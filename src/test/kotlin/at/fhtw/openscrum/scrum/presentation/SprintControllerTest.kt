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
class SprintControllerTest {
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

    @Test
    fun ensureListSprintsShowsSprintWorksProperly() {
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        sprintApplicationService.initializeSprint(
            InitializeSprintCommand(
                projectId = project.projectId.token,
                sprintLength = 2,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.sprint-list-item")))

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("Sprint 1")
        assertThat(pageSource).contains("Not Planned")

        webDriver.close()
    }

    /*
    Given a scrum master, a sprint, a sprint goal and product backlog items
    When the scrum master enters the information into the plan sprint form
    Then the sprint goal should be set and the product backlog items get commited to the sprint
     */
    @Test
    fun ensurePlanSprintWorksProperly() {
        // Given
        val sprintGoal = "Deliver the login feature"
        val itemTitle = "Item Title"
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
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null
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

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")
        webDriver.findElement(By.cssSelector("textarea#sprint-goal-input")).sendKeys(sprintGoal)

        val checkbox =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='productBacklogIds'][value='${productBacklogItem.productBacklogItemId}']"),
                ),
            )
        checkbox.click()

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#plan-sprint-form button"))).click()
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/projects/${sprint.projectId}/sprints/${sprint.sprintId}"))

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(sprintGoal)
        assertThat(pageSource).contains("In Progress")

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/kanban-board")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.sprint-backlog-item")))

        val kanbanPageSource = webDriver.pageSource
        assertThat(kanbanPageSource).contains(itemTitle)
        assertThat(kanbanPageSource).contains(itemDescription)

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/backlog")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))

        val productBacklogPageSource = webDriver.pageSource
        assertThat(productBacklogPageSource).contains(itemTitle)
        assertThat(productBacklogPageSource).contains(itemDescription)
        assertThat(productBacklogPageSource).contains("Commited to sprint")

        webDriver.close()
    }

    /*
    Given a scrum master of another project, a sprint, a sprint goal and product backlog items
    When the scrum master enters the information into the plan sprint form
    Then he receives an error that he is not the scrum master of this project
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenUserIsNotScrumMasterOfThisProject() {
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

        val scrumMaster1 =
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
                scrumMaster = scrumMaster1,
                developers = setOf(),
            )

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
                ),
            )

        val scrumMaster2Password = "abc123"
        val scrumMaster2 =
            userService.registerUser(
                authenticatedUser = admin,
                username = "scrum.master.other",
                firstName = "Other",
                lastName = "Master",
                password = scrumMaster2Password,
                email = "scrum.master.other@gmail.com",
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster2.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMaster2Password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")

        // Then
        assertThat(webDriver.pageSource).contains("404")

        webDriver.close()
    }

    /*
    Given a product owner, a sprint, a sprint goal and product backlog items
    When the product owner enters the information into the plan sprint form
    Then he receives an error that he has no permission to plan the sprint
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenUserIsProductOwner() {
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
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null
            }

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")

        // Then
        assertThat(webDriver.pageSource).contains("403")

        webDriver.close()
    }

    /*
    Given a developer, a sprint, a sprint goal and product backlog items
    When the developer enters the information into the plan sprint form
    Then he receives an error that he has no permission to plan the sprint
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenUserIsDeveloper() {
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getTeamMemberOfProject(project.projectId.token, developer.username) != null
            }

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")

        // Then
        assertThat(webDriver.pageSource).contains("403")

        webDriver.close()
    }

    /*
    Given a scrum master, a sprint with sprint status that is not "not planned", a sprint goal and product backlog items
    When the scrum master enters the information into the plan sprint form
    Then he receives an error that the sprint can not be planned
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenSprintIsAlreadyPlanned() {
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
            at.fhtw.openscrum.scrum.application.command.PlanSprintCommand(
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
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")

        // Then
        assertThat(webDriver.pageSource).contains("400")

        webDriver.close()
    }

    /*
    Given a scrum master, a sprint, a sprint goal and product backlog items that do not have status "in backlog"
    When the scrum master enters the information into the plan sprint form
    Then he receives an error that he can not commit already finished backlog items to a sprint
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenNoProductBacklogItemsAreInBacklog() {
        // Given
        val productBacklogItemTitle = "Implement Login"

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
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null
            }

        val productBacklogItem =
            productBacklogItemApplicationService.defineProductBacklogItem(
                productOwner.username,
                DefineProductBacklogItemCommand(
                    projectId = project.projectId.token,
                    title = productBacklogItemTitle,
                    description = "As a user, I want to log in to the application.",
                ),
            )

        productBacklogItemApplicationService.markAsCommittedToSprint(
            at.fhtw.openscrum.scrum.application.command.MarkAsCommitedToSprintCommand(
                projectId = project.projectId.token,
                productBacklogItemId = productBacklogItem.productBacklogItemId,
            ),
        )

        val sprint =
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = project.projectId.token,
                    sprintLength = 2,
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.cssSelector("p.no-product-backlog-item"))))

        // Then
        assertThat(webDriver.pageSource).doesNotContain(productBacklogItemTitle)

        webDriver.close()
    }

    /*
    Given a scrum master, a sprint, a blank sprint goal and product backlog items
    When the scrum master enters the information into the plan sprint form
    Then he receives an error that the sprint goal can not be blank
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenSprintGoalIsBlank() {
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

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")

        val checkbox =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='productBacklogIds'][value='${productBacklogItem.productBacklogItemId}']"),
                ),
            )
        checkbox.click()

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#plan-sprint-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))

        // Then
        assertThat(webDriver.pageSource).contains("Sprint goal must not be blank!")

        webDriver.close()
    }

    /*
    Given a scrum master, a sprint and a sprint goal and no product backlog items
    When the scrum master enters the information into the plan sprint form
    Then the scrum master should receive an error that the sprint backlog can not be empty
     */
    @Test
    fun ensurePlanSprintDoesNotWorkWhenNoProductBacklogItemsAreSelected() {
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

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

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMaster.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/sprints/${sprint.sprintId}/planning")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='productBacklogIds']")))

        webDriver.findElement(By.cssSelector("textarea#sprint-goal-input")).sendKeys("Deliver the login feature")

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#plan-sprint-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))

        // Then
        assertThat(webDriver.pageSource).contains("At least one product backlog item must be selected!")

        webDriver.close()
    }
}
