package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.application.command.MoveSprintBacklogItemCommand
import at.fhtw.openscrum.scrum.domain.model.sprint.MoveDirection
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus
import at.fhtw.openscrum.scrum.presentation.forms.PlanSprintForm
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal
import java.util.UUID

@Controller
@RequestMapping(SprintController.BASE_URL)
class SprintController(
    private val sprintApplicationService: SprintApplicationService,
    private val projectApplicationService: ProjectApplicationService,
    private val teamMemberApplicationService: TeamMemberApplicationService,
    private val log: Logger = LoggerFactory.getLogger(SprintController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects/{projectId}/sprints"
        const val PATH_INDEX = "/"
        const val FRAGMENT_SPRINT_LIST_ITEM = "/list"
        const val ROUTE_DETAILS = "/{sprintId}"
        const val ROUTE_PLANNING = "/{sprintId}/planning"
        const val ROUTE_KANBAN_BOARD = "/{sprintId}/kanban-board"
        const val FRAGMENT_SPRINT_BACKLOG_ITEMS = "/{sprintId}/backlog-items"
        const val ROUTE_MOVE_SPRINT_BACKLOG_ITEM = "/{sprintId}/move-backlog-item/{productBacklogItemId}"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        principal: Principal,
        @PathVariable projectId: UUID,
    ): String {
        log.debug("Serving list sprints page for project with id {}", projectId)
        teamMemberApplicationService.getTeamMemberOfProject(projectId, principal.name) ?: return "error/404"
        val project = projectApplicationService.getProject(projectId) ?: return "error/404"

        model.addAttribute("project", project)
        return "pages/list-sprints"
    }

    @HxRequest
    @GetMapping(value = [FRAGMENT_SPRINT_LIST_ITEM])
    fun getSprintListItems(
        model: Model,
        @PathVariable projectId: UUID,
    ): String {
        model.addAttribute("sprints", sprintApplicationService.getSprintsOfProject(projectId))
        return "fragments/sprint-list-item"
    }

    @GetMapping(value = [ROUTE_DETAILS])
    fun getSprintDetails(
        model: Model,
        principal: Principal,
        @PathVariable projectId: UUID,
        @PathVariable sprintId: UUID,
    ): String {
        log.debug("Serving sprint details for sprint with project id {} and sprint id {}", projectId, sprintId)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(projectId, principal.name) ?: return "error/404"
        val sprint = sprintApplicationService.getSprint(projectId, sprintId) ?: return "error/404"

        model.addAttribute("authenticatedTeamMember", authenticatedTeamMember)
        model.addAttribute("sprint", sprint)
        return "pages/sprint-details-page"
    }

    @GetMapping(value = [ROUTE_PLANNING])
    fun showPlanSprintForm(
        model: Model,
        principal: Principal,
        @PathVariable projectId: UUID,
        @PathVariable sprintId: UUID,
    ): String {
        log.debug("Serving plan sprint page for sprint with project id {} and sprint id {}", projectId, sprintId)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(projectId, principal.name) ?: return "error/404"
        if (!authenticatedTeamMember.isScrumMaster) return "error/403"
        val sprint = sprintApplicationService.getSprint(projectId, sprintId) ?: return "error/404"
        if (sprint.status.isPlanned) return "error/400"
        val planSprintForm = PlanSprintForm()

        model.addAttribute("sprint", sprint)
        model.addAttribute("planSprintForm", planSprintForm)
        return "pages/plan-sprint"
    }

    @PostMapping(value = [ROUTE_PLANNING])
    fun handlePlanSprintForm(
        model: Model,
        principal: Principal,
        @PathVariable projectId: UUID,
        @PathVariable sprintId: UUID,
        @Valid @ModelAttribute(name = "planSprintForm") form: PlanSprintForm,
        brPlanSprintForm: BindingResult,
    ): String {
        log.debug("Received http POST request to plan sprint with form {}", form)
        if (brPlanSprintForm.hasErrors()) {
            log.warn("Define plan sprint form {} has validation errors", form)
            val sprint = sprintApplicationService.getSprint(projectId, sprintId) ?: return "error/404"

            model.addAttribute("sprint", sprint)
            return "pages/plan-sprint"
        }

        try {
            sprintApplicationService.planSprint(principal.name, form.toDefinePlanSprintCommand(projectId, sprintId))
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while planning sprint with message: {}", ex.message)
            val sprint = sprintApplicationService.getSprint(projectId, sprintId) ?: return "error/404"

            model.addAttribute("sprint", sprint)
            model.addAttribute("errorMessage", ex.message)

            return "pages/plan-sprint"
        }
        return "redirect:/projects/$projectId/sprints/$sprintId"
    }

    @GetMapping(value = [ROUTE_KANBAN_BOARD])
    fun getSprintKanbanBoard(
        model: Model,
        principal: Principal,
        @PathVariable projectId: UUID,
        @PathVariable sprintId: UUID,
    ): String {
        log.debug("Serving sprint kanban board for sprint with project id {} and sprint id {}", projectId, sprintId)
        val authenticatedTeamMember =
            teamMemberApplicationService.getTeamMemberOfProject(projectId, principal.name) ?: return "error/404"
        val sprint = sprintApplicationService.getSprint(projectId, sprintId) ?: return "error/404"
        if (!sprint.status.isPlanned) return "error/404"

        model.addAttribute("authenticatedTeamMember", authenticatedTeamMember)
        model.addAttribute("sprint", sprint)
        return "pages/sprint-kanban-board-page"
    }

    @HxRequest
    @GetMapping(value = [FRAGMENT_SPRINT_BACKLOG_ITEMS])
    fun getSprintBacklogItems(
        model: Model,
        @PathVariable projectId: UUID,
        @PathVariable sprintId: UUID,
        @RequestParam status: SprintBacklogItemStatus,
    ): String {
        model.addAttribute(
            "sprintBacklogItems",
            sprintApplicationService.getSprintBacklogItems(projectId, sprintId, status),
        )
        return "fragments/sprint-backlog-item"
    }

    @HxRequest
    @PutMapping(value = [ROUTE_MOVE_SPRINT_BACKLOG_ITEM])
    fun moveSprintBacklogItem(
        model: Model,
        principal: Principal,
        @PathVariable projectId: UUID,
        @PathVariable sprintId: UUID,
        @PathVariable productBacklogItemId: UUID,
        @RequestParam moveDirection: MoveDirection,
    ): String {
        val sprintBacklogItem =
            sprintApplicationService.moveSprintBacklogItem(
                principal.name,
                MoveSprintBacklogItemCommand(projectId, sprintId, productBacklogItemId, moveDirection),
            )
        model.addAttribute("sprintBacklogItems", listOf(sprintBacklogItem))
        return "fragments/sprint-backlog-item"
    }
}
