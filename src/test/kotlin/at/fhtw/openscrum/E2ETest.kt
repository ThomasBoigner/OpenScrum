package at.fhtw.openscrum

import at.fhtw.openscrum.management.domain.model.project.ProjectService
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserRepository
import at.fhtw.openscrum.management.domain.model.user.UserService
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.project.ProjectEntityRepository
import at.fhtw.openscrum.management.infrastructure.persistence.jpa.user.UserEntityRepository
import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem.ProductBacklogItemEntityRepository
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.sprint.SprintEntityRepository
import at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember.TeamMemberEntityRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

/**
 * Base class for the Selenium end to end tests.
 *
 * Takes care of the boilerplate every controller test needs: the Spring Boot web environment, a clean database with a
 * freshly registered admin, a headless Chrome driver that is closed again after every test, and logging in.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("postgres")
abstract class E2ETest {
    @LocalServerPort
    var port: Int = 0

    val baseUrl: String
        get() = "http://localhost:$port"

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
    lateinit var userRepository: UserRepository

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

    lateinit var webDriver: ChromeDriver

    lateinit var wait: WebDriverWait

    lateinit var admin: User

    @BeforeEach
    fun setUpE2ETest() {
        sprintEntityRepository.deleteAll()
        productBacklogItemEntityRepository.deleteAll()
        teamMemberEntityRepository.deleteAll()
        scrumProjectEntityRepository.deleteAll()
        managementProjectEntityRepository.deleteAll()
        userEntityRepository.deleteAll()
        admin = userService.registerAdmin(ADMIN_PASSWORD)!!

        webDriver = createHeadlessChromeDriver()
        wait = WebDriverWait(webDriver, Duration.ofSeconds(5))
    }

    @AfterEach
    fun tearDownE2ETest() {
        webDriver.quit()
    }

    /**
     * Logs in via the login form and waits until the project list is reached. Only usable for credentials that are
     * expected to be accepted.
     */
    fun login(
        username: String,
        password: String,
    ) {
        webDriver.get(baseUrl)
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()
        wait.until(ExpectedConditions.urlContains("/projects"))
    }

    fun loginAsAdmin() = login(admin.username, ADMIN_PASSWORD)

    companion object {
        const val ADMIN_PASSWORD = "admin"
    }
}
