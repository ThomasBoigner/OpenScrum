package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.DeleteProductBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsCommitedToSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsDoneCommand
import at.fhtw.openscrum.scrum.application.command.UncommitFromSprintCommand
import at.fhtw.openscrum.scrum.application.command.UpdateProductBacklogItemCommand
import at.fhtw.openscrum.scrum.application.dtos.ProductBacklogItemStatusDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemService
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductBacklogItemApplicationServiceTest {
    lateinit var productBacklogItemApplicationService: ProductBacklogItemApplicationService

    @Mock
    lateinit var productBacklogItemService: ProductBacklogItemService

    @Mock
    lateinit var productBacklogItemRepository: ProductBacklogItemRepository

    @Mock
    lateinit var productOwnerRepository: ProductOwnerRepository

    @BeforeEach
    fun setUp() {
        productBacklogItemApplicationService =
            ProductBacklogItemApplicationService(
                productBacklogItemService,
                productBacklogItemRepository,
                productOwnerRepository,
            )
    }

    @Test
    fun ensureGetProductBacklogOfProjectWorksProperly() {
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

        whenever(productBacklogItemRepository.findProductBacklogItemsByProjectId(projectId))
            .thenReturn(listOf(item1, item2))

        // When
        val result = productBacklogItemApplicationService.getProductBacklogOfProject(projectId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].title).isEqualTo(item1.title)
        assertThat(result[0].description).isEqualTo(item1.description)
        assertThat(result[0].status).isEqualTo(ProductBacklogItemStatusDto.IN_BACKLOG)
        assertThat(result[1].title).isEqualTo(item2.title)
        assertThat(result[1].description).isEqualTo(item2.description)
        assertThat(result[1].status).isEqualTo(ProductBacklogItemStatusDto.IN_BACKLOG)
    }

    @Test
    fun ensureGetProductBacklogOfProjectWithStatusInBacklogWorksProperly() {
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

        whenever(
            productBacklogItemRepository.findProductBacklogItemsByProjectIdAndStatus(
                projectId,
                ProductBacklogItemStatus.IN_BACKLOG,
            ),
        ).thenReturn(listOf(item1, item2))

        // When
        val result = productBacklogItemApplicationService.getProductBacklogOfProjectWithStatusInBacklog(projectId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].title).isEqualTo(item1.title)
        assertThat(result[0].description).isEqualTo(item1.description)
        assertThat(result[0].status).isEqualTo(ProductBacklogItemStatusDto.IN_BACKLOG)
        assertThat(result[1].title).isEqualTo(item2.title)
        assertThat(result[1].description).isEqualTo(item2.description)
        assertThat(result[1].status).isEqualTo(ProductBacklogItemStatusDto.IN_BACKLOG)
    }

    @Test
    fun ensureDefineProductBacklogItemWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val username = "productowner"
        val command =
            DefineProductBacklogItemCommand(
                projectId = projectId,
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId = ProductBacklogItemId(projectId = projectId),
                title = command.title,
                description = command.description,
            )

        whenever(productOwnerRepository.findByProjectIdAndUsername(projectId, username)).thenReturn(productOwner)
        whenever(
            productBacklogItemService.defineBacklogItem(
                productOwner,
                projectId,
                command.title,
                command.description,
            ),
        ).thenReturn(productBacklogItem)

        // When
        val result = productBacklogItemApplicationService.defineProductBacklogItem(username, command)

        // Then
        assertThat(result.title).isEqualTo(command.title)
        assertThat(result.description).isEqualTo(command.description)
        assertThat(result.status).isEqualTo(ProductBacklogItemStatusDto.IN_BACKLOG)
    }

    @Test
    fun ensureUncommitFromSprintWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val command = UncommitFromSprintCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId =
                    ProductBacklogItemId(
                        projectId = projectId,
                        productBacklogItemId = productBacklogItemId,
                    ),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(productBacklogItem)
        whenever(productBacklogItemRepository.save(productBacklogItem)).thenReturn(productBacklogItem)

        // When
        val result = productBacklogItemApplicationService.uncommitFromSprint(command)

        // Then
        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(ProductBacklogItemStatusDto.IN_BACKLOG)
        assertThat(result.title).isEqualTo(productBacklogItem.title)
        assertThat(result.description).isEqualTo(productBacklogItem.description)
    }

    @Test
    fun ensureUncommitFromSprintReturnsNullWhenItemNotFound() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val command = UncommitFromSprintCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(null)

        // When
        val result = productBacklogItemApplicationService.uncommitFromSprint(command)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureMarkAsCommittedToSprintWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val command = MarkAsCommitedToSprintCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId =
                    ProductBacklogItemId(
                        projectId = projectId,
                        productBacklogItemId = productBacklogItemId,
                    ),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(productBacklogItem)
        whenever(productBacklogItemRepository.save(productBacklogItem)).thenReturn(productBacklogItem)

        // When
        val result = productBacklogItemApplicationService.markAsCommittedToSprint(command)

        // Then
        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(ProductBacklogItemStatusDto.COMMITED_TO_SPRINT)
        assertThat(result.title).isEqualTo(productBacklogItem.title)
        assertThat(result.description).isEqualTo(productBacklogItem.description)
    }

    @Test
    fun ensureMarkAsCommittedToSprintReturnsNullWhenItemNotFound() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val command = MarkAsCommitedToSprintCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(null)

        // When
        val result = productBacklogItemApplicationService.markAsCommittedToSprint(command)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureMarkAsCommitedToSprintDoneWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val command = MarkAsDoneCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId =
                    ProductBacklogItemId(
                        projectId = projectId,
                        productBacklogItemId = productBacklogItemId,
                    ),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(productBacklogItem)
        whenever(productBacklogItemRepository.save(productBacklogItem)).thenReturn(productBacklogItem)

        // When
        val result = productBacklogItemApplicationService.markAsCommitedToSprintDone(command)

        // Then
        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(ProductBacklogItemStatusDto.COMMITED_TO_SPRINT_DONE)
        assertThat(result.title).isEqualTo(productBacklogItem.title)
        assertThat(result.description).isEqualTo(productBacklogItem.description)
    }

    @Test
    fun ensureMarkAsCommitedToSprintDoneReturnsNullWhenItemNotFound() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val command = MarkAsDoneCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(null)

        // When
        val result = productBacklogItemApplicationService.markAsCommitedToSprintDone(command)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun ensureUpdateProductBacklogItemWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val username = "productowner"
        val command =
            UpdateProductBacklogItemCommand(
                projectId = projectId,
                productBacklogItemId = productBacklogItemId,
                title = "Updated Backlog",
                description = "As a product owner, I want to update the product backlog items.",
            )
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId =
                    ProductBacklogItemId(
                        projectId = projectId,
                        productBacklogItemId = productBacklogItemId,
                    ),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
            )
        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(productBacklogItem)
        whenever(productOwnerRepository.findByProjectIdAndUsername(projectId, username)).thenReturn(productOwner)
        whenever(productBacklogItemRepository.save(productBacklogItem)).thenReturn(productBacklogItem)

        // When
        val result = productBacklogItemApplicationService.updateProductBacklogItem(username, command)

        // Then
        assertThat(result.title).isEqualTo(command.title)
        assertThat(result.description).isEqualTo(command.description)
    }

    @Test
    fun ensureUpdateProductBacklogItemThrowsWhenItemDoesNotExist() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val username = "productowner"
        val command =
            UpdateProductBacklogItemCommand(
                projectId = projectId,
                productBacklogItemId = productBacklogItemId,
                title = "Updated Backlog",
                description = "As a product owner, I want to update the product backlog items.",
            )

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(null)

        // When
        assertThrows<NoSuchElementException> {
            productBacklogItemApplicationService.updateProductBacklogItem(username, command)
        }
    }

    @Test
    fun ensureDeleteProductBacklogItemWorksProperly() {
        // Given
        val projectId = UUID.randomUUID()
        val productBacklogItemId = UUID.randomUUID()
        val username = "productowner"
        val command =
            DeleteProductBacklogItemCommand(projectId = projectId, productBacklogItemId = productBacklogItemId)
        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = UUID.randomUUID(), projectId = projectId),
                username = username,
                fullName = FullName("First", "Last"),
            )
        val productBacklogItem =
            ProductBacklogItem(
                productBacklogItemId =
                    ProductBacklogItemId(
                        projectId = projectId,
                        productBacklogItemId = productBacklogItemId,
                    ),
                title = "Define Backlog",
                description = "As a product owner, I want to define the product backlog items.",
                status = ProductBacklogItemStatus.IN_BACKLOG,
            )

        whenever(
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(projectId = projectId, productBacklogItemId = productBacklogItemId),
            ),
        ).thenReturn(productBacklogItem)
        whenever(productOwnerRepository.findByProjectIdAndUsername(projectId, username)).thenReturn(productOwner)

        // When
        productBacklogItemApplicationService.deleteProductBacklogItem(username, command)

        // Then
        verify(productBacklogItemService).deleteProductBacklogItem(productOwner, productBacklogItem)
    }
}
