package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID

class ProductBacklogItemEntityMapperTest {
    lateinit var productBacklogItemEntityMapper: ProductBacklogItemEntityMapper

    @BeforeEach
    fun setUp() {
        productBacklogItemEntityMapper = ProductBacklogItemEntityMapper()
    }

    @Test
    fun ensureToProductBacklogItemPageWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val item1 =
            ProductBacklogItemEntity(
                id = 42,
                projectId = projectId,
                productBacklogItemId = UUID.randomUUID(),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )
        val item2 =
            ProductBacklogItemEntity(
                id = 43,
                projectId = projectId,
                productBacklogItemId = UUID.randomUUID(),
                title = "Implement Login",
                description = "As a user, I want to log in to the application.",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        val entityPage = PageImpl(mutableListOf(item1, item2), PageRequest.of(0, 10), 2)

        // When
        val result = productBacklogItemEntityMapper.toProductBacklogItemPage(entityPage)

        // Then
        assertThat(result.content).containsExactly(item1.toProductBacklogItem(), item2.toProductBacklogItem())
        assertThat(result.first).isTrue()
        assertThat(result.last).isTrue()
        assertThat(result.totalElements).isEqualTo(2)
    }
}
