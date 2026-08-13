package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.E2ETest
import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import java.util.UUID

class ProductBacklogControllerTest : E2ETest() {
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

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/define")
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

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/define")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
    }

    /*
    Given a developer, a title and a description
    When the developer enters the information into the define product backlog item form
    Then he receives an error that he has no permission to create a product backlog item
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenUserIsDeveloper() {
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

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/define")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
    }

    /*
    Given a scrum master, a title and a description
    When the scrum master enters the information into the define product backlog item form
    Then he receives an error that he has no permission to create a product backlog item
     */
    @Test
    fun ensureDefineProductBacklogItemDoesNotWorkWhenUserIsScrumMaster() {
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

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/define")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
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

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/define")
        webDriver.findElement(By.cssSelector("textarea#description")).sendKeys(description)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#define-product-backlog-item-form button")))
            .click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error.text).containsIgnoringCase("title")
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

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/define")
        webDriver.findElement(By.cssSelector("input#title")).sendKeys(title)
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#define-product-backlog-item-form button")))
            .click()

        // Then
        val error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))
        assertThat(error.text).containsIgnoringCase("description")
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

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get(
            "$baseUrl/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
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

        // When
        login(productOwner2.username, productOwner2Password)

        webDriver.get(
            "$baseUrl/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
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

        // When
        login(developer.username, developerPassword)

        webDriver.get(
            "$baseUrl/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
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

        // When
        login(scrumMaster.username, scrumMasterPassword)

        webDriver.get(
            "$baseUrl/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
        )

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("403")
    }

    /*
    Given a product owner, no backlog item, a title and a description
    When the product owner enters the information into the update product backlog item form of the item
    Then he receives an error that the product backlog item does not exist
     */
    @Test
    fun ensureUpdateProductBacklogItemDoesNotWorkWhenItemDoesNotExist() {
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

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(200))
            .until {
                teamMemberApplicationService.getProductOwnerOfProject(project.projectId.token) != null &&
                    teamMemberApplicationService.getScrumMasterOfProject(project.projectId.token) != null
            }

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog/${UUID.randomUUID()}/update")

        // Then
        val pageSource = webDriver.pageSource
        assertThat(pageSource).contains("404")
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

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get(
            "$baseUrl/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
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

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get(
            "$baseUrl/projects/${project.projectId.token}/backlog/${productBacklogItem.productBacklogItemId}/update",
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

        // When
        login(productOwner.username, productOwnerPassword)

        webDriver.get("$baseUrl/projects/${project.projectId.token}/backlog")
        val productBacklogListItem =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-backlog-list-item")))
        wait
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".product-backlog-list-item .delete-button")))
            .click()

        // Then
        wait.until(ExpectedConditions.stalenessOf(productBacklogListItem))
        assertThat(webDriver.findElements(By.cssSelector(".product-backlog-list-item"))).isEmpty()
    }
}
