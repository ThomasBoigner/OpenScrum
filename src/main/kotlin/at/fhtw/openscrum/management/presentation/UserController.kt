package at.fhtw.openscrum.management.presentation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(UserController.BASE_URL)
class UserController(
    private val log: Logger = LoggerFactory.getLogger(UserController::class.java),
) {
    companion object {
        const val BASE_URL = "/users"
        const val PATH_INDEX = "/"
        const val ROUTE_REGISTER = "/register"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(): String {
        log.debug("Serving list users page")
        return "pages/list-users"
    }

    @GetMapping(value = [ROUTE_REGISTER])
    fun showRegisterForm(): String {
        log.debug("Serving register user page")
        return "pages/register-user"
    }

    @PostMapping(value = [ROUTE_REGISTER])
    fun handleRegisterForm(): String = "redirect:$BASE_URL"
}
