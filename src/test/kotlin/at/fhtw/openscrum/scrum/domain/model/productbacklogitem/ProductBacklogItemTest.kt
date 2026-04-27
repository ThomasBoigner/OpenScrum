package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

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
}
