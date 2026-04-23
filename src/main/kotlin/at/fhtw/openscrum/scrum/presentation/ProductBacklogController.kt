package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping(ProductBacklogController.BASE_URL)
class ProductBacklogController(
    private val productBacklogApplicationService: ProductBacklogItemApplicationService,
    private val projectApplicationService: ProjectApplicationService,
    private val log: Logger = LoggerFactory.getLogger(ProductBacklogController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects/{id}/backlog"
        const val PATH_INDEX = "/"
        const val FRAGMENT_PRODUCT_BACKLOG_LIST_ITEM = "/list"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving list product backlog page for project with id {}", id)
        val project = projectApplicationService.getProject(id)

        model.addAttribute("project", project)

        return "pages/list-product-backlog"
    }

    @HxRequest
    @GetMapping(value = [FRAGMENT_PRODUCT_BACKLOG_LIST_ITEM])
    fun getProductBacklogListItems(
        model: Model,
        @PathVariable id: UUID,
    ): String {
        model.addAttribute("productBacklog", productBacklogApplicationService.getProductBacklogOfProject(id))
        return "fragments/product-backlog-list-item"
    }
}
