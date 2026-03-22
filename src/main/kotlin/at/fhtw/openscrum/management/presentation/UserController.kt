package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.management.application.UserApplicationService
import at.fhtw.openscrum.management.presentation.forms.RegisterUserForm
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
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
    fun getUsersListItems(model: Model): String {
        model.addAttribute("users", userApplicationService.getUsers())
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
}
