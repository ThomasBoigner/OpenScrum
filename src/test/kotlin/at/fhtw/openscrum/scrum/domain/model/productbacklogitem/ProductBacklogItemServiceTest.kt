package at.fhtw.openscrum.scrum.domain.model.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.EventPublisher
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import at.fhtw.openscrum.scrum.domain.util.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.never
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
    fun ensureGetProductBacklogItemsWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement Login",
                description = "As a user, I want to log in to the application.",
            )
        val productBacklogItemPage =
            Page(
                content = mutableListOf(productBacklogItem),
                last = true,
                totalPages = 2,
                totalElements = 6,
                first = false,
                size = 5,
                number = 1,
                numberOfElements = 1,
                empty = false,
            )

        whenever(productBacklogItemRepository.findProductBacklogItemsByProjectId(projectId, "login", 1, 5))
            .thenReturn(productBacklogItemPage)

        // When
        val result = productBacklogItemService.getProductBacklogItems(projectId, "login", 1, 5)

        // Then
        assertThat(result).isEqualTo(productBacklogItemPage)
    }

    @Test
    fun ensureGetProductBacklogItemsThrowsExceptionForNegativePage() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                productBacklogItemService.getProductBacklogItems(UUID.randomUUID(), page = -1)
            }

        // Then
        assertThat(exception.message).isEqualTo("Page number must not be negative!")
        verify(productBacklogItemRepository, never())
            .findProductBacklogItemsByProjectId(any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun ensureGetProductBacklogItemsThrowsExceptionForNonPositiveSize() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                productBacklogItemService.getProductBacklogItems(UUID.randomUUID(), size = 0)
            }

        // Then
        assertThat(exception.message).isEqualTo("Page size must be positive!")
        verify(productBacklogItemRepository, never())
            .findProductBacklogItemsByProjectId(any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun ensureGetProductBacklogItemsByStatusWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = "Implement Login",
                description = "As a user, I want to log in to the application.",
            )
        val productBacklogItemPage =
            Page(
                content = mutableListOf(productBacklogItem),
                last = true,
                totalPages = 2,
                totalElements = 6,
                first = false,
                size = 5,
                number = 1,
                numberOfElements = 1,
                empty = false,
            )

        whenever(
            productBacklogItemRepository.findProductBacklogItemsByProjectIdAndStatus(
                projectId,
                ProductBacklogItemStatus.IN_BACKLOG,
                1,
                5,
            ),
        ).thenReturn(productBacklogItemPage)

        // When
        val result =
            productBacklogItemService.getProductBacklogItemsByStatus(projectId, ProductBacklogItemStatus.IN_BACKLOG, 1, 5)

        // Then
        assertThat(result).isEqualTo(productBacklogItemPage)
    }

    @Test
    fun ensureGetProductBacklogItemsByStatusThrowsExceptionForNegativePage() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                productBacklogItemService.getProductBacklogItemsByStatus(
                    UUID.randomUUID(),
                    ProductBacklogItemStatus.IN_BACKLOG,
                    page = -1,
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Page number must not be negative!")
        verify(productBacklogItemRepository, never())
            .findProductBacklogItemsByProjectIdAndStatus(any(), any(), any(), any(), any())
    }

    @Test
    fun ensureGetProductBacklogItemsByStatusThrowsExceptionForNonPositiveSize() {
        // When
        val exception =
            assertThrows<IllegalArgumentException> {
                productBacklogItemService.getProductBacklogItemsByStatus(
                    UUID.randomUUID(),
                    ProductBacklogItemStatus.IN_BACKLOG,
                    size = 0,
                )
            }

        // Then
        assertThat(exception.message).isEqualTo("Page size must be positive!")
        verify(productBacklogItemRepository, never())
            .findProductBacklogItemsByProjectIdAndStatus(any(), any(), any(), any(), any())
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
