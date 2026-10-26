package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.util.Page
import org.springframework.stereotype.Component

@Component
class ProductBacklogItemEntityMapper {
    fun toProductBacklogItemPage(entityPage: org.springframework.data.domain.Page<ProductBacklogItemEntity>): Page<ProductBacklogItem> =
        Page(
            content = entityPage.content.map { it.toProductBacklogItem() }.toMutableList(),
            last = entityPage.isLast,
            totalPages = entityPage.totalPages,
            totalElements = entityPage.totalElements,
            first = entityPage.isFirst,
            size = entityPage.size,
            number = entityPage.number,
            numberOfElements = entityPage.numberOfElements,
            empty = entityPage.isEmpty,
        )
}
