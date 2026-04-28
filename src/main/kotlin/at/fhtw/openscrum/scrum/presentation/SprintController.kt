package at.fhtw.openscrum.scrum.presentation

import at.fhtw.openscrum.scrum.application.ProjectApplicationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping(SprintController.BASE_URL)
class SprintController(
    private val projectApplicationService: ProjectApplicationService,
    private val log: Logger = LoggerFactory.getLogger(SprintController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects/{id}/sprints"
        const val PATH_INDEX = "/"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        @PathVariable id: UUID,
    ): String {
        log.debug("Serving list sprints page for project with id {}", id)
        val project = projectApplicationService.getProject(id)

        model.addAttribute("project", project)
        return "pages/list-sprints"
    }
}
