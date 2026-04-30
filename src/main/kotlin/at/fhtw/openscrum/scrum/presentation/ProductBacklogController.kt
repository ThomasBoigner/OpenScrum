package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProductBacklogItemApplicationService
import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.presentation.forms.DefineProductBacklogItemForm
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal
import java.util.UUID

@Controller
@RequestMapping(ProductBacklogController.BASE_URL)
class ProductBacklogController(
    private val productBacklogApplicationService: ProductBacklogItemApplicationService,
    private val projectApplicationService: ProjectApplicationService,
    private val teamMemberApplicationService: TeamMemberApplicationService,
    private val log: Logger = LoggerFactory.getLogger(ProductBacklogController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects/{id}/backlog"
        const val PATH_INDEX = "/"
        const val FRAGMENT_PRODUCT_BACKLOG_LIST_ITEM = "/list"
        const val ROUTE_DEFINE = "/define"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving list product backlog page for project with id {}", id)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(id, principal.name) ?: return "error/404"
        val project = projectApplicationService.getProject(id) ?: return "error/404"

        model.addAttribute("authenticatedTeamMember", authenticatedTeamMember)
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

    @GetMapping(value = [ROUTE_DEFINE])
    fun showDefineBacklogItemForm(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving list define product backlog item page for project with id {}", id)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(id, principal.name) ?: return "error/404"
        if (!authenticatedTeamMember.isProductOwner) return "error/403"
        val project = projectApplicationService.getProject(id) ?: return "error/404"
        val defineProductBacklogItemForm = DefineProductBacklogItemForm()

        model.addAttribute("project", project)
        model.addAttribute("defineProductBacklogItemForm", defineProductBacklogItemForm)
        return "pages/define-product-backlog-item"
    }

    @PostMapping(value = [ROUTE_DEFINE])
    fun handleDefineBacklogItemForm(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
        @Valid @ModelAttribute(name = "defineProductBacklogItemForm") form: DefineProductBacklogItemForm,
        brDefineProductBacklogItemForm: BindingResult,
    ): String {
        log.debug("Received http POST request to define product backlog item with form {}", form)
        if (brDefineProductBacklogItemForm.hasErrors()) {
            log.warn("Define product backlog item form {} has validation errors", form)
            val project = projectApplicationService.getProject(id) ?: return "error/404"
            model.addAttribute("project", project)
            return "pages/define-product-backlog-item"
        }
        try {
            productBacklogApplicationService.defineProductBacklogItem(
                principal.name,
                form.toDefineProductBacklogItemCommand(id),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while defining product backlog item with message: {}", ex.message)
            val project = projectApplicationService.getProject(id) ?: return "error/404"

            model.addAttribute("project", project)
            model.addAttribute("errorMessage", ex.message)

            return "pages/define-product-backlog-item"
        }
        return "redirect:/projects/$id/backlog"
    }
}
