package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.DefineProductBacklogItemCommand
import at.fhtw.openscrum.scrum.application.dtos.ProductBacklogItemDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemService
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProductBacklogItemApplicationService(
    private val productBacklogItemService: ProductBacklogItemService,
    private val productBacklogItemRepository: ProductBacklogItemRepository,
    private val productOwnerRepository: ProductOwnerRepository,
    private val log: Logger = LoggerFactory.getLogger(ProductBacklogItemApplicationService::class.java),
) {
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
}
