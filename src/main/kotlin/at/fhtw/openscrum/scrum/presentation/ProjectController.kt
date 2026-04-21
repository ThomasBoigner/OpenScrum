package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.DefineDefinitionOfDoneCommand
import at.fhtw.openscrum.scrum.application.command.DefineProductGoalCommand
import at.fhtw.openscrum.scrum.application.command.DefineSprintLengthCommand
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal
import java.util.UUID

@Controller("scrumProjectController")
@RequestMapping(ProjectController.BASE_URL)
class ProjectController(
    private val projectApplicationService: ProjectApplicationService,
    private val teamMemberApplicationService: TeamMemberApplicationService,
    private val log: Logger = LoggerFactory.getLogger(ProjectController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects/{id}"
        const val PATH_INDEX = "/"
        const val ROUTE_CONFIGURE = "/configure"
        const val ROUTE_CONFIGURE_SPRINT_LENGTH = "$ROUTE_CONFIGURE/sprint-length"
        const val ROUTE_CONFIGURE_PRODUCT_GOAL = "$ROUTE_CONFIGURE/product-goal"
        const val ROUTE_CONFIGURE_DEFINITION_OF_DONE = "$ROUTE_CONFIGURE/definition-of-done"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving project details page for id {}", id)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(id, principal.name) ?: return "error/404"
        val project = projectApplicationService.getProject(id) ?: return "error/404"
        val developers = teamMemberApplicationService.getDevelopersOfProject(id)
        val scrumMaster = teamMemberApplicationService.getScrumMasterOfProject(id) ?: return "error/404"
        val productOwner = teamMemberApplicationService.getProductOwnerOfProject(id) ?: return "error/404"

        model.addAttribute("authenticatedTeamMember", authenticatedTeamMember)
        model.addAttribute("project", project)
        model.addAttribute("developers", developers)
        model.addAttribute("scrumMaster", scrumMaster)
        model.addAttribute("productOwner", productOwner)

        return "pages/project-details-page"
    }

    @GetMapping(value = [ROUTE_CONFIGURE])
    fun showConfigurationForm(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving project configuration page for id {}", id)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(id, principal.name) ?: return "error/404"
        if (!authenticatedTeamMember.isScrumMaster && !authenticatedTeamMember.isProductOwner) return "error/403"
        val project = projectApplicationService.getProject(id) ?: return "error/404"

        model.addAttribute("authenticatedTeamMember", authenticatedTeamMember)
        model.addAttribute("project", project)

        return "pages/configure-project"
    }

    @HxRequest
    @PutMapping(value = [ROUTE_CONFIGURE_SPRINT_LENGTH])
    fun configureSprintLength(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
        sprintLength: Int,
    ): String {
        log.debug(
            "Received http PUT request to update sprint length of project with id {} to sprint length {}",
            id,
            sprintLength,
        )
        try {
            projectApplicationService.defineSprintLength(
                principal.name,
                DefineSprintLengthCommand(id, sprintLength),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while configuring sprint length with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            return "fragments/error-message"
        }
        model.addAttribute("message", "Saved sprint length")
        return "fragments/saved-changes"
    }

    @HxRequest
    @PutMapping(value = [ROUTE_CONFIGURE_PRODUCT_GOAL])
    fun configureProductGoal(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
        productGoal: String,
    ): String {
        log.debug(
            "Received http PUT request to update product goal of project with id {} to product goal {}",
            id,
            productGoal,
        )
        try {
            projectApplicationService.defineProductGoal(
                principal.name,
                DefineProductGoalCommand(id, productGoal),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while configuring product goal with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            return "fragments/error-message"
        }
        model.addAttribute("message", "Saved product goal")
        return "fragments/saved-changes"
    }

    @HxRequest
    @PutMapping(value = [ROUTE_CONFIGURE_DEFINITION_OF_DONE])
    fun configureDefinitionOfDone(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
        definitionOfDone: String,
    ): String {
        log.debug(
            "Received http PUT request to update definition of done of project with id {} to definition of done {}",
            id,
            definitionOfDone,
        )
        try {
            projectApplicationService.defineDefinitionOfDone(
                principal.name,
                DefineDefinitionOfDoneCommand(id, definitionOfDone),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while configuring definition of done with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            return "fragments/error-message"
        }
        model.addAttribute("message", "Saved definition of done")
        return "fragments/saved-changes"
    }
}
