package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.EventPublisher
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductBacklogItemServiceTest {
    lateinit var productBacklogItemService: ProductBacklogItemService

    @Mock
    lateinit var productBacklogItemRepository: ProductBacklogItemRepository

    @Mock
    lateinit var eventPublisher: EventPublisher

    @BeforeEach
    fun setUp() {
        productBacklogItemService = ProductBacklogItemService(productBacklogItemRepository, eventPublisher)
    }

    @Test
    fun ensureDefineBacklogItemWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val title = "Define Backlog"
        val description = "As a product owner, I want to define the product backlog items."

        whenever(productBacklogItemRepository.save(any())).thenAnswer { it.arguments[0] }

        // When
        val productBacklogItem =
            productBacklogItemService.defineBacklogItem(productOwner, projectId, title, description)

        // Then
        assertThat(productBacklogItem.title).isEqualTo(title)
        assertThat(productBacklogItem.description).isEqualTo(description)
        assertThat(productBacklogItem.status).isEqualTo(ProductBacklogItemStatus.IN_BACKLOG)
    }

    @Test
    fun ensureDefineBacklogItemThrowsWhenProductOwnerBelongsToAnotherProject() {
        // Given
        val projectId = UUID.randomUUID()
        val anotherProjectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = anotherProjectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val title = "Define Backlog"
        val description = "As a product owner, I want to define the product backlog items."

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.defineBacklogItem(productOwner, projectId, title, description)
        }
    }

    @Test
    fun ensureDefineBacklogItemThrowsWhenTitleIsBlank() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val title = ""
        val description = "As a product owner, I want to define the product backlog items."

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.defineBacklogItem(productOwner, projectId, title, description)
        }
    }

    @Test
    fun ensureDefineBacklogItemThrowsWhenDescriptionIsBlank() {
        // Given
        val projectId = UUID.randomUUID()
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = "productowner",
                fullName = FullName("First", "Last"),
            )
        val title = "Define Backlog"
        val description = ""

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.defineBacklogItem(productOwner, projectId, title, description)
        }
    }

    @Test
    fun ensureDeleteProductBacklogItemWorksProperly() {
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
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        // When
        productBacklogItemService.deleteProductBacklogItem(productOwner, productBacklogItem)

        // Then
        verify(productBacklogItemRepository).delete(productBacklogItem.productBacklogItemId)
        verify(eventPublisher).publishEvent(any())
    }

    @Test
    fun ensureDeleteProductBacklogItemThrowsWhenProductOwnerBelongsToAnotherProject() {
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
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.deleteProductBacklogItem(productOwner, productBacklogItem)
        }
    }

    @Test
    fun ensureDeleteProductBacklogItemThrowsWhenCallerIsScrumMaster() {
        // Given
        val projectId = UUID.randomUUID()

        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.deleteProductBacklogItem(null, productBacklogItem)
        }
    }

    @Test
    fun ensureDeleteProductBacklogItemThrowsWhenCallerIsDeveloper() {
        // Given
        val projectId = UUID.randomUUID()

        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.deleteProductBacklogItem(null, productBacklogItem)
        }
    }

    @Test
    fun ensureDeleteProductBacklogItemThrowsWhenItemIsCommittedToSprint() {
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
                status = ProductBacklogItemStatus.COMMITTED_TO_SPRINT,
            )

        // When
        assertThrows<IllegalArgumentException> {
            productBacklogItemService.deleteProductBacklogItem(productOwner, productBacklogItem)
        }
    }
}
