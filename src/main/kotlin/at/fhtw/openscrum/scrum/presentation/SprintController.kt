package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
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
        log.debug("Serving sprint details")
        val authenticatedTeamMember = teamMemberApplicationService.getTeamMemberOfProject(projectId, principal.name) ?: return "error/404"
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
        val authenticatedTeamMember = teamMemberApplicationService.getTeamMemberOfProject(projectId, principal.name) ?: return "error/404"
        if (!authenticatedTeamMember.isScrumMaster) return "error/403"
        sprintApplicationService.getSprint(projectId, sprintId) ?: return "error/404"
        return "pages/plan-sprint"
    }
}
