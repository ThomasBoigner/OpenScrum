package at.fhtw.openscrum.management.domain.model.project

import at.fhtw.openscrum.management.domain.model.user.EmailAddress
import at.fhtw.openscrum.management.domain.model.user.FullName
import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import at.fhtw.openscrum.management.domain.model.user.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProjectTest {
    @Test
    fun ensureProjectNameCanNotBeBlank() {
        // Given
        val projectName = ""

        // When
        assertThrows<IllegalArgumentException> {
            Project(
                projectName = projectName,
                developerIds = setOf(UserId()),
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
            )
        }
    }

    @Test
    fun ensureUpdateWorksProperly() {
        // Given
        val oldProductOwnerId = UserId()
        val oldScrumMasterId = UserId()
        val oldDeveloperId = UserId()

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = oldProductOwnerId,
                scrumMasterId = oldScrumMasterId,
                developerIds = setOf(oldDeveloperId),
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        // When
        project.update(
            projectName = "OpenScrum 2",
            productOwner = productOwner,
            scrumMaster = scrumMaster,
            developers = setOf(developer),
        )

        // Then
        assertThat(project.projectName).isEqualTo("OpenScrum 2")
        assertThat(project.productOwnerId).isEqualTo(productOwner.userId)
        assertThat(project.scrumMasterId).isEqualTo(scrumMaster.userId)
        assertThat(project.developerIds).containsExactly(developer.userId)

        assertThat(project.projectInformationChangedEvents).hasSize(1)
        assertThat(project.projectInformationChangedEvents.first().projectName).isEqualTo("OpenScrum 2")

        assertThat(project.productOwnerUnassignedEvents).hasSize(1)
        assertThat(project.productOwnerUnassignedEvents.first().userId).isEqualTo(oldProductOwnerId)
        assertThat(project.productOwnerAssignedEvents).hasSize(1)
        assertThat(project.productOwnerAssignedEvents.first().userId).isEqualTo(productOwner.userId)

        assertThat(project.scrumMasterUnassignedEvents).hasSize(1)
        assertThat(project.scrumMasterUnassignedEvents.first().userId).isEqualTo(oldScrumMasterId)
        assertThat(project.scrumMasterAssignedEvents).hasSize(1)
        assertThat(project.scrumMasterAssignedEvents.first().userId).isEqualTo(scrumMaster.userId)

        assertThat(project.developerUnassignedEvents).hasSize(1)
        assertThat(project.developerUnassignedEvents.first().userId).isEqualTo(oldDeveloperId)
        assertThat(project.developerAssignedEvents).hasSize(1)
        assertThat(project.developerAssignedEvents.first().userId).isEqualTo(developer.userId)
    }

    @Test
    fun ensureUpdateDoesNotPublishEventsWhenTeamDoesNotChange() {
        // Given
        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val developer =
            User(
                username = "developer",
                emailAddress = EmailAddress("developer@gmail.com"),
                fullName = FullName("Dev", "Eloper"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = productOwner.userId,
                scrumMasterId = scrumMaster.userId,
                developerIds = setOf(developer.userId),
            )

        // When
        project.update(
            projectName = "OpenScrum 2",
            productOwner = productOwner,
            scrumMaster = scrumMaster,
            developers = setOf(developer),
        )

        // Then
        assertThat(project.projectName).isEqualTo("OpenScrum 2")
        assertThat(project.projectInformationChangedEvents).hasSize(1)
        assertThat(project.productOwnerUnassignedEvents).isEmpty()
        assertThat(project.productOwnerAssignedEvents).isEmpty()
        assertThat(project.scrumMasterUnassignedEvents).isEmpty()
        assertThat(project.scrumMasterAssignedEvents).isEmpty()
        assertThat(project.developerUnassignedEvents).isEmpty()
        assertThat(project.developerAssignedEvents).isEmpty()
    }

    @Test
    fun ensureUpdateDoesNotPublishProjectInformationChangedWhenNameIsUnchanged() {
        // Given
        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = productOwner.userId,
                scrumMasterId = scrumMaster.userId,
                developerIds = setOf(),
            )

        // When
        project.update(
            projectName = "OpenScrum",
            productOwner = productOwner,
            scrumMaster = scrumMaster,
            developers = setOf(),
        )

        // Then
        assertThat(project.projectName).isEqualTo("OpenScrum")
        assertThat(project.projectInformationChangedEvents).isEmpty()
    }

    @Test
    fun ensureUpdateThrowsExceptionWhenProjectNameIsBlank() {
        // Given
        val project =
            Project(
                projectName = "OpenScrum",
                productOwnerId = UserId(),
                scrumMasterId = UserId(),
                developerIds = setOf(UserId()),
            )

        val productOwner =
            User(
                username = "productOwner",
                emailAddress = EmailAddress("product.owner@gmail.com"),
                fullName = FullName("Product", "Owner"),
                password = "password",
                role = Role.USER,
            )

        val scrumMaster =
            User(
                username = "scrumMaster",
                emailAddress = EmailAddress("scrum.master@gmail.com"),
                fullName = FullName("Scrum", "Master"),
                password = "password",
                role = Role.USER,
            )

        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                project.update(
                    projectName = "",
                    productOwner = productOwner,
                    scrumMaster = scrumMaster,
                    developers = setOf(),
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Project name must not be blank!")
        assertThat(project.projectName).isEqualTo("OpenScrum")
    }
}
