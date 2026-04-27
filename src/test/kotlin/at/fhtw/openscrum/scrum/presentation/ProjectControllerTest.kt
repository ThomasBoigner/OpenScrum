package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.createHeadlessChromeDriver
import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.project.ProjectEntityRepository
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember.TeamMemberEntityRepository
import org.assertj.core.api.Assertions.assertThat
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
class ProjectControllerTest {
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

    @BeforeEach
    fun cleanUp() {
        teamMemberEntityRepository.deleteAll()
        scrumProjectEntityRepository.deleteAll()
        managementProjectEntityRepository.deleteAll()
        userEntityRepository.deleteAll()
        userService.registerAdmin()
    }

    /*
    Given a scrum master, a project and a sprint length
    When the scrum master enters the information into the configure project form
    Then the sprint length should be set
     */
    @Test
    fun ensureConfigureSprintLengthWorksProperly() {
        // Given
        val sprintLength = "3"

        val admin = userEntityRepository.findByUsername("admin")!!.toUser()

        val productOwnerUser =
            userService.registerUser(
                authenticatedUser = admin,
                username = "product.owner",
                firstName = "Product",
                lastName = "Owner",
                password = "abc123",
                email = "product.owner@gmail.com",
            )

        val scrumMasterPassword = "abc123"
        val scrumMasterUser =
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
                productOwner = productOwnerUser,
                scrumMaster = scrumMasterUser,
                developers = setOf(),
            )

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(scrumMasterUser.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(scrumMasterPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("input#sprint-length")).clear()
        webDriver.findElement(By.cssSelector("input#sprint-length")).sendKeys(sprintLength)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#sprint-length-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(sprintLength)
        webDriver.close()
    }

    /*
    Given a scrum master of another project, a project and a sprint length smaller than 1
    When the scrum master enters the information into the configure project form
    Then he receives an error that he is not the scrum master of this project
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenUserIsNotScrumMasterOfThisProject() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a developer, a project and a sprint length
    When the developer enters the information into the configure project form
    Then he receives an error that he has no permission to change the sprint length
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenUserIsDeveloper() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a scrum master, a project and a sprint length smaller than 1
    When the scrum master enters the information into the configure project form
    Then he receives an error that the sprint length can not be smaller than 1
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenSprintLengthIsSmallerThanOne() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.executeScript("document.getElementById('sprint-length').removeAttribute('min')")
        webDriver.findElement(By.cssSelector("input#sprint-length")).clear()
        webDriver.findElement(By.cssSelector("input#sprint-length")).sendKeys("0")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#sprint-length-form button"))).click()

        // Then
        val error =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("sprint length")
        webDriver.close()
    }

    /*
    Given a scrum master, a project and a sprint length bigger than 4
    When the scrum master enters the information into the configure project form
    Then he receives an error that the sprint length can not be bigger than 4
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenSprintLengthIsBiggerThanFour() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.executeScript("document.getElementById('sprint-length').removeAttribute('max')")
        webDriver.findElement(By.cssSelector("input#sprint-length")).clear()
        webDriver.findElement(By.cssSelector("input#sprint-length")).sendKeys("5")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#sprint-length-form button"))).click()

        // Then
        val error =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("sprint length")
        webDriver.close()
    }

    /*
    Given a product owner, a project and a product goal
    When the product owner enters the information into the configure project form
    Then the product goal should be set
     */
    @Test
    fun ensureConfigureProductGoalWorksProperly() {
        // Given
        val productGoal = "Build the best scrum tool"

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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#product-goal")).clear()
        webDriver.findElement(By.cssSelector("textarea#product-goal")).sendKeys(productGoal)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#product-goal-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(productGoal)
        webDriver.close()
    }

    /*
    Given a product owner of another project, a project and a product goal
    When the product owner enters the information into the configure project form
    Then he receives an error that he is not the product owner of this project
     */
    @Test
    fun ensureConfigureProductGoalDoesNotWorkWhenUserIsNotProductOwnerOfThisProject() {
        // Given
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a developer, a project and a product goal
    When the developer enters the information into the configure project form
    Then he receives an error that he has no permission to change the product goal
     */
    @Test
    fun ensureConfigureProductGoalDoesNotWorkWhenUserIsDeveloper() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a product owner, a project and a blank product goal
    When the product owner enters the information into the configure project form
    Then the product goal should be null
     */
    @Test
    fun ensureConfigureProductGoalWithBlankValueSetsProductGoalToNull() {
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

        val webDriver = createHeadlessChromeDriver()
        val wait = WebDriverWait(webDriver, Duration.ofSeconds(5))

        // When
        webDriver.get("http://localhost:8080")
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(productOwner.username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(productOwnerPassword)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#product-goal")).clear()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#product-goal-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).doesNotContain("Product Goal")
        webDriver.close()
    }

    /*
    Given a scrum master, a project and a definition of done
    When the scrum master enters the information into the configure project form
    Then the definition of done should be set
     */
    @Test
    fun ensureConfigureDefinitionOfDoneWorksProperly() {
        // Given
        val definitionOfDone = "All tests pass and code is reviewed"

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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#definition-of-done")).clear()
        webDriver.findElement(By.cssSelector("textarea#definition-of-done")).sendKeys(definitionOfDone)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#definition-of-done-form button")))
            .click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(definitionOfDone)
        webDriver.close()
    }

    /*
    Given a scrum master of another project, a project and a definition of done
    When the scrum master enters the information into the configure project form
    Then he receives an error that he is not the scrum master of this project
     */
    @Test
    fun ensureConfigureDefinitionOfDoneDoesNotWorkWhenUserIsNotScrumMasterOfThisProject() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
        webDriver.close()
    }

    /*
    Given a developer, a project and a definition of done
    When the developer enters the information into the configure project form
    Then he receives an error that he has no permission to change the definition of done
     */
    @Test
    fun ensureConfigureDefinitionOfDoneDoesNotWorkWhenUserIsDeveloper() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
        webDriver.close()
    }

    /*
    Given a scrum master, a project and a blank definition of done
    When the scrum master enters the information into the configure project form
    Then the definition of done should be null
     */
    @Test
    fun ensureConfigureDefinitionOfDoneWithBlankValueSetsDefinitionOfDoneToNull() {
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#definition-of-done")).clear()
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#definition-of-done-form button")))
            .click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).doesNotContain("Definition of Done")
        webDriver.close()
    }
}
