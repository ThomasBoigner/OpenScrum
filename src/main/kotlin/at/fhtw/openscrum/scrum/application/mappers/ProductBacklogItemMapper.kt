package at.fhtw.openscrum.scrum.application.mappers

import at.fhtw.openscrum.scrum.application.dtos.ProductBacklogItemDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.util.Page
import org.springframework.stereotype.Component

@Component
class ProductBacklogItemMapper {
    fun toProductBacklogItemDtoPage(productBacklogItemPage: Page<ProductBacklogItem>): Page<ProductBacklogItemDto> =
        Page(
            content = productBacklogItemPage.content.map { ProductBacklogItemDto(it) }.toMutableList(),
            last = productBacklogItemPage.last,
            totalPages = productBacklogItemPage.totalPages,
            totalElements = productBacklogItemPage.totalElements,
            first = productBacklogItemPage.first,
            size = productBacklogItemPage.size,
            number = productBacklogItemPage.number,
            numberOfElements = productBacklogItemPage.numberOfElements,
            empty = productBacklogItemPage.empty,
        )
}
