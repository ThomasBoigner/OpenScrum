package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.E2ETest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

class ProjectControllerTest : E2ETest() {
    /*
    Given a scrum master, a project and a sprint length
    When the scrum master enters the information into the configure project form
    Then the sprint length should be set
     */
    @Test
    fun ensureConfigureSprintLengthWorksProperly() {
        // Given
        val sprintLength = "3"

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

        // When
        login(scrumMasterUser.username, scrumMasterPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("input#sprint-length")).clear()
        webDriver.findElement(By.cssSelector("input#sprint-length")).sendKeys(sprintLength)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#sprint-length-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("$baseUrl/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(sprintLength)
    }

    /*
    Given a scrum master of another project, a project and a sprint length smaller than 1
    When the scrum master enters the information into the configure project form
    Then he receives an error that he is not the scrum master of this project
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenUserIsNotScrumMasterOfThisProject() {
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

        // When
        login(scrumMaster2.username, scrumMaster2Password)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
    }

    /*
    Given a developer, a project and a sprint length
    When the developer enters the information into the configure project form
    Then he receives an error that he has no permission to change the sprint length
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenUserIsDeveloper() {
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

        // When
        login(developer.username, developerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
    }

    /*
    Given a scrum master, a project and a sprint length smaller than 1
    When the scrum master enters the information into the configure project form
    Then he receives an error that the sprint length can not be smaller than 1
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenSprintLengthIsSmallerThanOne() {
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

        // When
        login(scrumMaster.username, scrumMasterPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.executeScript("document.getElementById('sprint-length').removeAttribute('min')")
        webDriver.findElement(By.cssSelector("input#sprint-length")).clear()
        webDriver.findElement(By.cssSelector("input#sprint-length")).sendKeys("0")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#sprint-length-form button"))).click()

        // Then
        val error =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("sprint length")
    }

    /*
    Given a scrum master, a project and a sprint length bigger than 4
    When the scrum master enters the information into the configure project form
    Then he receives an error that the sprint length can not be bigger than 4
     */
    @Test
    fun ensureConfigureSprintLengthDoesNotWorkWhenSprintLengthIsBiggerThanFour() {
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

        // When
        login(scrumMaster.username, scrumMasterPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.executeScript("document.getElementById('sprint-length').removeAttribute('max')")
        webDriver.findElement(By.cssSelector("input#sprint-length")).clear()
        webDriver.findElement(By.cssSelector("input#sprint-length")).sendKeys("5")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#sprint-length-form button"))).click()

        // Then
        val error =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.error-message")))
        assertThat(error).isNotNull
        assertThat(error.text).containsIgnoringCase("sprint length")
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

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#product-goal")).clear()
        webDriver.findElement(By.cssSelector("textarea#product-goal")).sendKeys(productGoal)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#product-goal-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("$baseUrl/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(productGoal)
    }

    /*
    Given a product owner of another project, a project and a product goal
    When the product owner enters the information into the configure project form
    Then he receives an error that he is not the product owner of this project
     */
    @Test
    fun ensureConfigureProductGoalDoesNotWorkWhenUserIsNotProductOwnerOfThisProject() {
        // Given
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

        // When
        login(productOwner2.username, productOwner2Password)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
    }

    /*
    Given a developer, a project and a product goal
    When the developer enters the information into the configure project form
    Then he receives an error that he has no permission to change the product goal
     */
    @Test
    fun ensureConfigureProductGoalDoesNotWorkWhenUserIsDeveloper() {
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

        // When
        login(developer.username, developerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
    }

    /*
    Given a product owner, a project and a blank product goal
    When the product owner enters the information into the configure project form
    Then the product goal should be null
     */
    @Test
    fun ensureConfigureProductGoalWithBlankValueSetsProductGoalToNull() {
        // Given
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

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#product-goal")).clear()
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#product-goal-form button"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("$baseUrl/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).doesNotContain("Product Goal")
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

        // When
        login(scrumMaster.username, scrumMasterPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#definition-of-done")).clear()
        webDriver.findElement(By.cssSelector("textarea#definition-of-done")).sendKeys(definitionOfDone)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#definition-of-done-form button")))
            .click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("$baseUrl/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains(definitionOfDone)
    }

    /*
    Given a scrum master of another project, a project and a definition of done
    When the scrum master enters the information into the configure project form
    Then he receives an error that he is not the scrum master of this project
     */
    @Test
    fun ensureConfigureDefinitionOfDoneDoesNotWorkWhenUserIsNotScrumMasterOfThisProject() {
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

        // When
        login(scrumMaster2.username, scrumMaster2Password)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
    }

    /*
    Given a developer, a project and a definition of done
    When the developer enters the information into the configure project form
    Then he receives an error that he has no permission to change the definition of done
     */
    @Test
    fun ensureConfigureDefinitionOfDoneDoesNotWorkWhenUserIsDeveloper() {
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

        // When
        login(developer.username, developerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
    }

    /*
    Given a scrum master, a project and a blank definition of done
    When the scrum master enters the information into the configure project form
    Then the definition of done should be null
     */
    @Test
    fun ensureConfigureDefinitionOfDoneWithBlankValueSetsDefinitionOfDoneToNull() {
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

        // When
        login(scrumMaster.username, scrumMasterPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/configure")
        webDriver.findElement(By.cssSelector("textarea#definition-of-done")).clear()
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("form#definition-of-done-form button")))
            .click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#message.saved-changes")))

        webDriver.get("$baseUrl/projects/${project.projectId.token}")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).doesNotContain("Definition of Done")
    }
}
