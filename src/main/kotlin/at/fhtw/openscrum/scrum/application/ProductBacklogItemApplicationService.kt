package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsCommitedToSprintCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsDoneCommand
import at.fhtw.openscrum.scrum.application.command.MarkAsInBacklogCommand
import at.fhtw.openscrum.scrum.application.dtos.ProductBacklogItemDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemService
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.collections.map

@Service
@Transactional(readOnly = true)
class ProductBacklogItemApplicationService(
    private val productBacklogItemService: ProductBacklogItemService,
    private val productBacklogItemRepository: ProductBacklogItemRepository,
    private val productOwnerRepository: ProductOwnerRepository,
    private val log: Logger = LoggerFactory.getLogger(ProductBacklogItemApplicationService::class.java),
) {
    fun getProductBacklogOfProject(projectId: UUID): List<ProductBacklogItemDto> {
        log.debug("Trying to get all product backlog items of project with id {}", projectId)
        val productBacklogItems = productBacklogItemRepository.findProductBacklogItemsByProjectId(projectId)
        log.info("Found all ({}) product backlog items of project with id {}", productBacklogItems.size, projectId)
        return productBacklogItems.map { ProductBacklogItemDto(it) }
    }

    fun getProductBacklogOfProjectWithStatusInBacklog(projectId: UUID): List<ProductBacklogItemDto> {
        log.debug(
            "Trying to get all product backlog items of project with id {} that have status in backlog",
            projectId,
        )
        val productBacklogItems =
            productBacklogItemRepository.findProductBacklogItemsByProjectIdAndStatus(
                projectId,
                ProductBacklogItemStatus.IN_BACKLOG,
            )
        log.info(
            "Found all ({}) product backlog items of project with id {} that have status in backlog",
            productBacklogItems.size,
            projectId,
        )
        return productBacklogItems.map { ProductBacklogItemDto(it) }
    }

    @Transactional(readOnly = false)
    fun defineProductBacklogItem(
        authenticatedUserUsername: String,
        command: DefineProductBacklogItemCommand,
    ): ProductBacklogItemDto {
        log.debug("Trying to define product backlog item with command: {}", command)

        val productOwner =
            productOwnerRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        return ProductBacklogItemDto(
            productBacklogItemService.defineBacklogItem(
                productOwner,
                command.projectId,
                command.title,
                command.description,
            ),
        )
    }

    @Transactional(readOnly = false)
    fun markAsInBacklog(command: MarkAsInBacklogCommand): ProductBacklogItemDto? {
        log.debug("Trying to mark product backlog item as in backlog with command: {}", command)
        val productBacklogItem =
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(
                    projectId = command.projectId,
                    productBacklogItemId = command.productBacklogItemId,
                ),
            )

        if (productBacklogItem == null) {
            log.error(
                "Cannot mark as in backlog, product backlog with projectId {} and productBacklogItemId {} does not exist",
                command.projectId,
                command.productBacklogItemId,
            )
            return null
        }

        productBacklogItem.setStatusToInBacklog()
        return ProductBacklogItemDto(productBacklogItemRepository.save(productBacklogItem))
    }

    @Transactional(readOnly = false)
    fun markAsCommittedToSprint(command: MarkAsCommitedToSprintCommand): ProductBacklogItemDto? {
        log.debug("Trying to mark product backlog item as commited to sprint with command: {}", command)
        val productBacklogItem =
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(
                    projectId = command.projectId,
                    productBacklogItemId = command.productBacklogItemId,
                ),
            )

        if (productBacklogItem == null) {
            log.error(
                "Cannot mark as commited to sprint, product backlog with projectId {} and productBacklogItemId {} does not exist",
                command.projectId,
                command.productBacklogItemId,
            )
            return null
        }

        productBacklogItem.setStatusToCommittedToSprint()
        return ProductBacklogItemDto(productBacklogItemRepository.save(productBacklogItem))
    }

    @Transactional(readOnly = false)
    fun markAsCommitedToSprintDone(command: MarkAsDoneCommand): ProductBacklogItemDto? {
        log.debug("Trying to mark product backlog item as done with command: {}", command)
        val productBacklogItem =
            productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                ProductBacklogItemId(
                    projectId = command.projectId,
                    productBacklogItemId = command.productBacklogItemId,
                ),
            )

        if (productBacklogItem == null) {
            log.error(
                "Cannot mark as done, product backlog with projectId {} and productBacklogItemId {} does not exist",
                command.projectId,
                command.productBacklogItemId,
            )
            return null
        }

        productBacklogItem.setStatusToCommittedToSprintDone()
        return ProductBacklogItemDto(productBacklogItemRepository.save(productBacklogItem))
    }
}
