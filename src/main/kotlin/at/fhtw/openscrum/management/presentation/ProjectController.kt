package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.application.ProjectApplicationService
import at.fhtw.openscrum.management.application.UserApplicationService
import at.fhtw.openscrum.management.presentation.forms.CreateProjectForm
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal

@Controller
@RequestMapping(ProjectController.BASE_URL)
class ProjectController(
    private val projectApplicationService: ProjectApplicationService,
    private val userApplicationService: UserApplicationService,
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
    fun showCreationForm(model: Model): String {
        log.debug("Serving create project page")
        model.addAttribute("createProjectForm", CreateProjectForm())
        model.addAttribute("users", userApplicationService.getUsers())
        return "pages/create-project"
    }

    @PostMapping(value = [ROUTE_CREATE])
    fun handleCreationForm(
        principal: Principal,
        @Valid @ModelAttribute(name = "createProjectForm") form: CreateProjectForm,
        brCreateProjectForm: BindingResult,
        model: Model,
    ): String {
        log.debug("Received http POST request to create project with form {}", form)
        if (brCreateProjectForm.hasErrors()) {
            log.warn("Create project form {} has validation errors", form)
            model.addAttribute("users", userApplicationService.getUsers())
            return "pages/create-project"
        }

        try {
            projectApplicationService.createProject(
                principal.name,
                form.toCreateProjectCommand(),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while creating project with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            model.addAttribute("users", userApplicationService.getUsers())
            return "pages/create-project"
        }

        return "redirect:$BASE_URL"
    }
}
