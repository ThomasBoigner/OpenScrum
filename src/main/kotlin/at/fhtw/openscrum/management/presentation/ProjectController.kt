package at.fhtw.openscrum.management.presentation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(ProjectController.BASE_URL)
class ProjectController(
    private val log: Logger = LoggerFactory.getLogger(ProjectController::class.java),
) {
    companion object {
        const val BASE_URL = "/projects"
        const val PATH_INDEX = "/"
        const val ROUTE_CREATE = "/create"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(): String {
        log.debug("Serving list projects page")
        return "pages/list-projects"
    }

    @GetMapping(value = [ROUTE_CREATE])
    fun showCreationForm(): String = "pages/create-project"
}
