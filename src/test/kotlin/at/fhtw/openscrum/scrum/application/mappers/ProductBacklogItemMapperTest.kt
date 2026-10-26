package at.fhtw.openscrum.scrum.application.mappers

import at.fhtw.openscrum.scrum.application.dtos.ProductBacklogItemDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.util.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ProductBacklogItemMapperTest {
    lateinit var productBacklogItemMapper: ProductBacklogItemMapper

    @BeforeEach
    fun setUp() {
        productBacklogItemMapper = ProductBacklogItemMapper()
    }

    @Test
    fun ensureToProductBacklogItemDtoPageMapsContent() {
        // Given
        val projectId = UUID.randomUUID()
        val item1 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        val item2 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement Login",
                description = "As a user, I want to log in to the application.",
            )

        val productBacklogItemPage =
            Page(
                content = mutableListOf(item1, item2),
                last = true,
                totalPages = 1,
                totalElements = 2,
                first = true,
                size = 10,
                number = 0,
                numberOfElements = 2,
                empty = false,
            )

        // When
        val result = productBacklogItemMapper.toProductBacklogItemDtoPage(productBacklogItemPage)

        // Then
        assertThat(result.content).containsExactly(ProductBacklogItemDto(item1), ProductBacklogItemDto(item2))
        assertThat(result.last).isEqualTo(productBacklogItemPage.last)
        assertThat(result.totalPages).isEqualTo(productBacklogItemPage.totalPages)
        assertThat(result.totalElements).isEqualTo(productBacklogItemPage.totalElements)
        assertThat(result.first).isEqualTo(productBacklogItemPage.first)
        assertThat(result.size).isEqualTo(productBacklogItemPage.size)
        assertThat(result.number).isEqualTo(productBacklogItemPage.number)
        assertThat(result.numberOfElements).isEqualTo(productBacklogItemPage.numberOfElements)
        assertThat(result.empty).isEqualTo(productBacklogItemPage.empty)
    }
}
