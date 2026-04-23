package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
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
}