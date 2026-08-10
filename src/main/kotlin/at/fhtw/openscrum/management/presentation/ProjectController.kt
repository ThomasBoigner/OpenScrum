package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.application.ProjectApplicationService
import at.fhtw.openscrum.management.application.UserApplicationService
import at.fhtw.openscrum.management.presentation.forms.CreateProjectForm
import at.fhtw.openscrum.management.presentation.forms.UpdateProjectForm
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

@Controller("managementProjectController")
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
        const val ROUTE_UPDATE = "/{projectId}/update"
        const val FRAGMENT_PROJECTS_LIST_ITEM = "/list"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        principal: Principal,
        model: Model,
    ): String {
        log.debug("Serving list projects page")
        model.addAttribute(
            "authenticatedUser",
            userApplicationService.getUserByUsername(principal.name),
        )
        return "pages/list-projects"
    }

    @HxRequest
    @GetMapping(value = [FRAGMENT_PROJECTS_LIST_ITEM])
    fun getProjectsListItems(
        principal: Principal,
        model: Model,
    ): String {
        try {
            model.addAttribute("projects", projectApplicationService.getProjects(principal.name))
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while trying to get all projects with message: {}", ex.message)
            return "redirect:htmx:/error/400"
        }
        model.addAttribute(
            "authenticatedUser",
            userApplicationService.getUserByUsername(principal.name),
        )
        return "fragments/project-list-item"
    }

    @GetMapping(value = [ROUTE_CREATE])
    fun showCreationForm(
        principal: Principal,
        model: Model,
    ): String {
        log.debug("Serving create project page")
        model.addAttribute("createProjectForm", CreateProjectForm())
        model.addAttribute("users", userApplicationService.getUsers(principal.name))
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
            model.addAttribute("users", userApplicationService.getUsers(principal.name))
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
            model.addAttribute("users", userApplicationService.getUsers(principal.name))
            return "pages/create-project"
        }

        return "redirect:$BASE_URL"
    }

    @GetMapping(value = [ROUTE_UPDATE])
    fun showUpdateForm(
        principal: Principal,
        @PathVariable projectId: UUID,
        model: Model,
    ): String {
        log.debug("Serving update project page for project with id {}", projectId)
        val project = projectApplicationService.getProject(projectId) ?: return "error/404"
        val updateProjectForm =
            UpdateProjectForm(
                projectName = project.projectName,
                productOwnerId = project.productOwnerId,
                scrumMasterId = project.scrumMasterId,
                developerIds = project.developerIds,
            )
        model.addAttribute("updateProjectForm", updateProjectForm)
        model.addAttribute("projectId", projectId)
        model.addAttribute("users", userApplicationService.getUsers(principal.name))
        return "pages/update-project"
    }

    @PostMapping(value = [ROUTE_UPDATE])
    fun handleUpdateForm(
        principal: Principal,
        @PathVariable projectId: UUID,
        @Valid @ModelAttribute(name = "updateProjectForm") form: UpdateProjectForm,
        brUpdateProjectForm: BindingResult,
        model: Model,
    ): String {
        log.debug("Received http POST request to update project with id {} with form {}", projectId, form)
        if (brUpdateProjectForm.hasErrors()) {
            log.warn("Update project form {} has validation errors", form)
            model.addAttribute("projectId", projectId)
            model.addAttribute("users", userApplicationService.getUsers(principal.name))
            return "pages/update-project"
        }

        try {
            projectApplicationService.updateProject(
                principal.name,
                form.toUpdateProjectCommand(projectId),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while updating project with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            model.addAttribute("projectId", projectId)
            model.addAttribute("users", userApplicationService.getUsers(principal.name))
            return "pages/update-project"
        }

        return "redirect:$BASE_URL"
    }
}
