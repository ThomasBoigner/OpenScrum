package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.productbacklogitem

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class ProductBacklogItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var projectId: UUID,
    var productBacklogItemId: UUID,
    var title: String,
    var description: String,
    @Enumerated(EnumType.STRING)
    var status: ProductBacklogItemStatus,
) {
    constructor(productBacklogItem: ProductBacklogItem) : this(
        id = productBacklogItem.id,
        projectId = productBacklogItem.productBacklogItemId.projectId,
        productBacklogItemId = productBacklogItem.productBacklogItemId.productBacklogItemId,
        title = productBacklogItem.title,
        description = productBacklogItem.description,
        status = productBacklogItem.status,
    )

    fun toProductBacklogItem(): ProductBacklogItem =
        ProductBacklogItem(
            id = id,
            productBacklogItemId =
                ProductBacklogItemId(
                    projectId = projectId,
                    productBacklogItemId = productBacklogItemId,
                ),
            title = title,
            description = description,
            status = status,
        )
}
