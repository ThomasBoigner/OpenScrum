package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ProductBacklogItemTest {
    @Test
    fun ensureProductBacklogItemIsCreated() {
        // Given
        val projectId = UUID.randomUUID()
        val title = "Define Backlog"
        val description = "As a product owner, I want to define the product backlog items."

        // When
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = title,
                description = description,
            )

        // Then
        assertThat(productBacklogItem.title).isEqualTo(title)
        assertThat(productBacklogItem.description).isEqualTo(description)
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.IN_BACKLOG)
    }

    @Test
    fun ensureProductBacklogItemCanNotBeCreatedWithBlankTitle() {
        // Given
        val projectId = UUID.randomUUID()
        val title = ""
        val description = "As a product owner, I want to define the product backlog items."

        // When
        assertThrows<IllegalArgumentException> {
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = title,
                description = description,
            )
        }
    }

    @Test
    fun ensureProductBacklogItemCanNotBeCreatedWithBlankDescription() {
        // Given
        val projectId = UUID.randomUUID()
        val title = "Define Backlog"
        val description = ""

        // When
        assertThrows<IllegalArgumentException> {
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = title,
                description = description,
            )
        }
    }

    @Test
    fun ensureUpdateWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        val title = "Update Backlog"
        val description = "As a product owner, I want to update the product backlog items."

        // When
        productBacklogItem.update(productOwner, title, description)

        // Then
        assertThat(productBacklogItem.title).isEqualTo(title)
        assertThat(productBacklogItem.description).isEqualTo(description)
    }

    @Test
    fun ensureUpdateThrowsWhenProductOwnerBelongsToAnotherProject() {
        // Given
        val projectId = UUID.randomUUID()
        val anotherProjectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = anotherProjectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItem.update(productOwner, "Update Backlog", "Updated description")
        }
    }

    @Test
    fun ensureUpdateThrowsWhenCallerIsScrumMaster() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItem.update(null, "Update Backlog", "Updated description")
        }
    }

    @Test
    fun ensureUpdateThrowsWhenCallerIsDeveloper() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItem.update(null, "Update Backlog", "Updated description")
        }
    }

    @Test
    fun ensureUpdateThrowsWhenTitleIsBlank() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItem.update(productOwner, "", "Updated description")
        }
    }

    @Test
    fun ensureUpdateThrowsWhenDescriptionIsBlank() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItem.update(productOwner, "Update Backlog", "")
        }
    }

    @Test
    fun ensureProductBacklogSetStatusToCommittedToSprintWorksProperly() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "Description",
            )

        // When
        productBacklogItem.setStatusToCommittedToSprint()

        // Then
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.COMMITTED_TO_SPRINT)
    }

    @Test
    fun ensureProductBacklogSetStatusToCommitedToSprintDoneWorksProperly() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "Description",
            )

        // When
        productBacklogItem.setStatusToCommittedToSprintDone()

        // Then
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.COMMITTED_TO_SPRINT_DONE)
    }

    @Test
    fun ensureUncommitFromSprintWorksProperlyWhenStatusIsCommittedToSprint() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "Description",
                status = ProductBacklogItemStatus.COMMITTED_TO_SPRINT,
            )

        // When
        productBacklogItem.uncommitFromSprint()

        // Then
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.IN_BACKLOG)
    }

    @Test
    fun ensureUncommitFromSprintWorksProperlyWhenStatusIsCommittedToSprintDone() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "Description",
                status = ProductBacklogItemStatus.COMMITTED_TO_SPRINT_DONE,
            )

        // When
        productBacklogItem.uncommitFromSprint()

        // Then
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.DONE)
    }

    @Test
    fun ensureUncommitFromSprintWorksProperlyWhenStatusIsOtherStatus() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "Description",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        // When
        productBacklogItem.uncommitFromSprint()

        // Then
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.IN_BACKLOG)
    }

    @Test
    fun ensureProductBacklogItemCanNotBeCreatedWithTooLongTitle() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                    title = "t".repeat(256),
                    description = "As a product owner, I want to define the product backlog items.",
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Title must not be longer than 255 characters!")
    }

    @Test
    fun ensureProductBacklogItemCanNotBeCreatedWithTooLongDescription() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                ProductBacklogItem(
                    productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                    title = "Define Backlog",
                    description = "d".repeat(4096),
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Description must not be longer than 4095 characters!")
    }

    @Test
    fun ensureUpdateThrowsWhenTitleIsTooLong() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        val titleException =
            assertThrows<IllegalArgumentException> {
                productBacklogItem.update(productOwner, "t".repeat(256), "Updated description")
            }

        // Then
        assertThat(titleException.message).isEqualTo("Title must not be longer than 255 characters!")
    }

    @Test
    fun ensureUpdateThrowsWhenDescriptionIsTooLong() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        val descriptionException =
            assertThrows<IllegalArgumentException> {
                productBacklogItem.update(productOwner, "Update Backlog", "d".repeat(4096))
            }

        // Then
        assertThat(descriptionException.message).isEqualTo("Description must not be longer than 4095 characters!")
    }
}
