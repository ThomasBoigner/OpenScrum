package at.fhtw.openscrum.scrum.presentation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller("scrumProjectController")
@RequestMapping(ProjectController.BASE_URL)
class ProjectController(
    private val log: Logger = LoggerFactory.getLogger(ProjectController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects/{id}"
        const val PATH_INDEX = "/"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        model: Model,
        @PathVariable id: UUID,
    ): String = "pages/project-details-page"
}
