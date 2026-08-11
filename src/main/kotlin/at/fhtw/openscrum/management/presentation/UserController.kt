package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.application.UserApplicationService
import at.fhtw.openscrum.management.application.command.DeleteUserCommand
import at.fhtw.openscrum.management.application.command.DemoteUserCommand
import at.fhtw.openscrum.management.application.command.PromoteUserCommand
import at.fhtw.openscrum.management.application.dtos.UserDto
import at.fhtw.openscrum.management.presentation.forms.RegisterUserForm
import at.fhtw.openscrum.management.presentation.forms.UpdateUserForm
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import java.security.Principal
import java.util.UUID

@Controller
@RequestMapping(UserController.BASE_URL)
class UserController(
    private val userApplicationService: UserApplicationService,
    private val log: Logger = LoggerFactory.getLogger(UserController::class.java),
) {
    companion object {
        const val BASE_URL = "/users"
        const val PATH_INDEX = "/"
        const val ROUTE_REGISTER = "/register"
        const val FRAGMENT_USERS_LIST_ITEM = "/list"
        const val ROUTE_UPDATE_USER = "/{userId}/update"
        const val ROUTE_DELETE_USER = "/{userId}/delete"
        const val ROUTE_PROMOTE_USER = "/{userId}/promote"
        const val ROUTE_DEMOTE_USER = "/{userId}/demote"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        principal: Principal,
        model: Model,
    ): String {
        log.debug("Serving list users page")
        model.addAttribute(
            "authenticatedUser",
            userApplicationService.getUserByUsername(principal.name),
        )
        return "pages/list-users"
    }

    @HxRequest
    @GetMapping(value = [FRAGMENT_USERS_LIST_ITEM])
    fun getUsersListItems(
        principal: Principal,
        model: Model,
    ): String {
        model.addAttribute("users", userApplicationService.getUsers(principal.name))
        model.addAttribute(
            "authenticatedUser",
            userApplicationService.getUserByUsername(principal.name),
        )
        return "fragments/users-list-item"
    }

    @GetMapping(value = [ROUTE_REGISTER])
    fun showRegisterForm(model: Model): String {
        log.debug("Serving register user page")
        model.addAttribute("registerUserForm", RegisterUserForm())
        return "pages/register-user"
    }

    @PostMapping(value = [ROUTE_REGISTER])
    fun handleRegisterForm(
        principal: Principal,
        @Valid @ModelAttribute(name = "registerUserForm") form: RegisterUserForm,
        brRegisterUserForm: BindingResult,
        model: Model,
    ): String {
        log.debug("Received http POST request to register user with form {}", form)
        if (brRegisterUserForm.hasErrors()) {
            log.warn("Register user form {} has validation errors", form)
            return "pages/register-user"
        }

        try {
            userApplicationService.registerUser(
                principal.name,
                form.toRegisterUserCommand(),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while registering user with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            return "pages/register-user"
        }

        return "redirect:$BASE_URL"
    }

    @GetMapping(value = [ROUTE_UPDATE_USER])
    fun showUpdateUserForm(
        principal: Principal,
        @PathVariable userId: UUID,
        model: Model,
    ): String {
        log.debug("Serving update user page for user with id {}", userId)

        val authenticatedUser = userApplicationService.getUserByUsername(principal.name)
        if (authenticatedUser?.role?.isManager == false && authenticatedUser.userId != userId) return "error/403"

        val user = userApplicationService.getUserByUserId(userId) ?: return "error/404"
        val userUpdateForm =
            UpdateUserForm(
                username = user.username,
                email = user.emailAddress,
                firstName = user.firstName,
                lastName = user.lastName,
                password = "",
            )
        model.addAttribute(
            "updateUserForm",
            userUpdateForm,
        )
        model.addAttribute("userId", userId)
        return "pages/update-user"
    }

    @PostMapping(value = [ROUTE_UPDATE_USER])
    fun handleUpdateUserForm(
        principal: Principal,
        @PathVariable userId: UUID,
        @Valid @ModelAttribute(name = "updateUserForm") form: UpdateUserForm,
        brUpdateUserForm: BindingResult,
        model: Model,
        request: HttpServletRequest,
    ): String {
        log.debug("Received http POST request to update user with id {} with form {}", userId, form)
        if (brUpdateUserForm.hasErrors()) {
            log.warn("Update user form {} has validation errors", form)
            model.addAttribute("userId", userId)
            return "pages/update-user"
        }

        val authenticatedUser = userApplicationService.getUserByUsername(principal.name)

        try {
            userApplicationService.updateUser(
                principal.name,
                form.toUpdateUserCommand(userId),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while updating user with message: {}", ex.message)
            model.addAttribute("errorMessage", ex.message)
            model.addAttribute("userId", userId)
            return "pages/update-user"
        }

        if (authenticatedUser?.userId == userId) {
            log.debug("User {} updated their own account, logging out", principal.name)
            request.logout()
            return "redirect:/login"
        }

        return "redirect:$BASE_URL"
    }

    @HxRequest
    @PostMapping(value = [ROUTE_PROMOTE_USER])
    fun promoteUser(
        principal: Principal,
        @PathVariable userId: UUID,
        model: Model,
        response: HttpServletResponse,
    ): String {
        log.debug("Received http POST request to promote user with id {}", userId)
        try {
            val user = userApplicationService.promoteUser(principal.name, PromoteUserCommand(userId))
            model.addAttribute("users", listOfNotNull(user))
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while promoting user with message: {}", ex.message)
            model.addAttribute("users", emptyList<UserDto>())
            response.setHeader("HX-Reswap", "none")
        }
        model.addAttribute(
            "authenticatedUser",
            userApplicationService.getUserByUsername(principal.name),
        )
        return "fragments/users-list-item"
    }

    @HxRequest
    @PostMapping(value = [ROUTE_DEMOTE_USER])
    fun demoteUser(
        principal: Principal,
        @PathVariable userId: UUID,
        model: Model,
        response: HttpServletResponse,
    ): String {
        log.debug("Received http POST request to demote user with id {}", userId)
        try {
            val user = userApplicationService.demoteUser(principal.name, DemoteUserCommand(userId))
            model.addAttribute("users", listOf(user))
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while demoting user with message: {}", ex.message)
            model.addAttribute("users", emptyList<UserDto>())
            response.setHeader("HX-Reswap", "none")
        }
        model.addAttribute(
            "authenticatedUser",
            userApplicationService.getUserByUsername(principal.name),
        )
        return "fragments/users-list-item"
    }

    @HxRequest
    @DeleteMapping(value = [ROUTE_DELETE_USER])
    @ResponseStatus(value = HttpStatus.OK)
    fun deleteUser(
        principal: Principal,
        @PathVariable userId: UUID,
    ) {
        log.debug("Received http DELETE request to delete user with id {}", userId)
        try {
            userApplicationService.deleteUser(
                principal.name,
                DeleteUserCommand(userId),
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Error while deleting user with message: {}", ex.message)
        }
    }
}
