package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import at.fhtw.openscrum.management.presentation.createHeadlessChromeDriver
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.CreateProjectCommand
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.project.ProjectEntityRepository
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember.TeamMemberEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
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
class ProjectControllerTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var userEntityRepository: UserEntityRepository

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

        val webDriver = ChromeDriver()
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

        webDriver.get("http://localhost:8080/projects/${project.projectId.token}")

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.team-info")))
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(sprintLength)
        webDriver.close()
    }

    
}
