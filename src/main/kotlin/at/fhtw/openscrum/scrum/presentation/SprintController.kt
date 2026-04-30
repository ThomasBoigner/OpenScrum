package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import at.fhtw.openscrum.scrum.application.SprintApplicationService
import at.fhtw.openscrum.scrum.application.TeamMemberApplicationService
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
        const val BASE_URL = "/projects/{id}/sprints"
        const val PATH_INDEX = "/"
        const val FRAGMENT_SPRINT_LIST_ITEM = "/list"
        const val ROUTE_DETAILS = "/{sprintId}"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        principal: Principal,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving list sprints page for project with id {}", id)
        teamMemberApplicationService.getTeamMemberOfProject(id, principal.name) ?: return "error/404"
        val project = projectApplicationService.getProject(id) ?: return "error/404"

        model.addAttribute("project", project)
        return "pages/list-sprints"
    }

    @HxRequest
    @GetMapping(value = [FRAGMENT_SPRINT_LIST_ITEM])
    fun getSprintListItems(
        model: Model,
        @PathVariable id: UUID,
    ): String {
        model.addAttribute("sprints", sprintApplicationService.getSprintsOfProject(id))
        return "fragments/sprint-list-item"
    }

    @GetMapping(value = [ROUTE_DETAILS])
    fun getSprintDetails(
        model: Model,
        @PathVariable id: UUID,
        @PathVariable sprintId: UUID,
    ): String {
        log.debug("Serving sprint details")
        return "pages/sprint-details-page"
    }
}
