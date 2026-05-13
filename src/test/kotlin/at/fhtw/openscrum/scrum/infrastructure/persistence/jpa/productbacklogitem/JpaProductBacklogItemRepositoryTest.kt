package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@ActiveProfiles("postgres")
class JpaProductBacklogItemRepositoryTest {
    @Autowired
    lateinit var productBacklogItemRepository: ProductBacklogItemRepository

    @Autowired
    lateinit var productBacklogItemEntityRepository: ProductBacklogItemEntityRepository

    @BeforeEach
    fun cleanUp() {
        productBacklogItemEntityRepository.deleteAll()
    }

    @Test
    fun ensureSaveWorksProperly() {
        // Given
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID()),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        // When
        productBacklogItemRepository.save(productBacklogItem)

        // Then
        val savedEntities = productBacklogItemEntityRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        val savedItem = savedEntities.first().toProductBacklogItem()
        assertThat(savedItem).isEqualTo(productBacklogItem)
    }

    @Test
    fun ensureFindProductBacklogItemByProductBacklogItemIdWorksProperly() {
        // Given
        val productBacklogItemId = ProductBacklogItemId(projectId = UUID.randomUUID())
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = productBacklogItemId,
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        productBacklogItemRepository.save(productBacklogItem)

        // When
        val result = productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(productBacklogItemId)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(productBacklogItem)
    }

    @Test
    fun ensureFindProductBacklogItemsByProjectIdWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        val productBacklogItem2 =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Other Backlog Item",
                description = "As a product owner, I want to define other backlog items.",
            )
        productBacklogItemRepository.save(productBacklogItem)
        productBacklogItemRepository.save(productBacklogItem2)

        // When
        val result = productBacklogItemRepository.findProductBacklogItemsByProjectId(projectId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(listOf(productBacklogItem, productBacklogItem2))
    }

    @Test
    fun ensureFindProductBacklogItemsByProjectIdAndStatusWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val inBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )
        val committedItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement Login",
                description = "As a user, I want to log in to the application.",
                status = ProductBacklogItemStatus.COMMITTED_TO_SPRINT,
            )
        productBacklogItemRepository.save(inBacklogItem)
        productBacklogItemRepository.save(committedItem)

        // When
        val result =
            productBacklogItemRepository.findProductBacklogItemsByProjectIdAndStatus(
                projectId,
                ProductBacklogItemStatus.IN_BACKLOG,
            )

        // Then
        assertThat(result).hasSize(1)
        assertThat(result).isEqualTo(listOf(inBacklogItem))
    }

    @Test
    fun ensureDeleteWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        productBacklogItemRepository.save(productBacklogItem)

        // When
        productBacklogItemRepository.delete(productBacklogItem.productBacklogItemId)

        // Then
        assertThat(productBacklogItemRepository.findProductBacklogItemsByProjectId(projectId)).isEmpty()
    }
}
