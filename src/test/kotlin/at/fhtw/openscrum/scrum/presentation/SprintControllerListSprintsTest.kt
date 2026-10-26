package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.E2ETest
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class SprintControllerListSprintsTest : E2ETest() {
    @Test
    fun ensureSearchSprintsWorksProperly() {
        // Given
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
        val projectId = project.projectId.token

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getScrumMasterOfProject(projectId) != null &&
                    sprintApplicationService.getSprintsOfProject(projectId).totalElements == 1L
            }

        val numberOfSprints = 3
        repeat(numberOfSprints - 1) {
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = projectId,
                    sprintLength = 2,
                ),
            )
        }

        // When
        login("scrum.master", scrumMasterPassword)
        webDriver.get("$baseUrl/projects/$projectId/sprints")
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("div.sprint-list-item"), 3))
        val firstRow = webDriver.findElement(By.cssSelector("div.sprint-list-item"))
        webDriver.findElement(By.cssSelector("input#search-input")).sendKeys("SPRINT 2")
        // the search response replaces the list, so the old first row becomes stale once the search has been answered
        wait.until(ExpectedConditions.stalenessOf(firstRow))

        // Then
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("div.sprint-list-item"), 1))
        val names = webDriver.findElements(By.cssSelector("div.sprint-list-item h2")).map { it.text }
        assertThat(names).containsExactly("Sprint 2")
    }

    @Test
    fun ensurePaginateSprintsWorksProperly() {
        // Given
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
        val projectId = project.projectId.token

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getScrumMasterOfProject(projectId) != null &&
                    // creating a project schedules its first sprint
                    sprintApplicationService.getSprintsOfProject(projectId).totalElements == 1L
            }

        val pageSize = 5

        repeat(pageSize + 1) {
            sprintApplicationService.initializeSprint(
                InitializeSprintCommand(
                    projectId = projectId,
                    sprintLength = 2,
                ),
            )
        }

        val loaderSelector = "#sprint-list-items [hx-trigger='intersect once']"

        webDriver.manage().window().size = Dimension(1920, 600)

        // When
        login("scrum.master", scrumMasterPassword)
        webDriver.get("$baseUrl/projects/$projectId/sprints")
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("div.sprint-list-item"), pageSize))
        assertThat(webDriver.findElements(By.cssSelector(loaderSelector))).hasSize(1)

        // scroll to the end of the sprint list
        webDriver.executeScript("document.querySelector(arguments[0]).scrollIntoView()", loaderSelector)

        // Then
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("div.sprint-list-item"), pageSize + 2))
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(loaderSelector), 0))
        val names = webDriver.findElements(By.cssSelector("div.sprint-list-item h2")).map { it.text }
        assertThat(names.reversed()).isEqualTo((1..pageSize + 2).map { "Sprint $it" })
    }
}
