package at.fhtw.openscrum.scrum.infrastructure.configuration

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ScrumBeanConfiguration {
    @Bean
    fun productBacklogItemService(productBacklogItemRepository: ProductBacklogItemRepository): ProductBacklogItemService =
        ProductBacklogItemService(productBacklogItemRepository)
}
