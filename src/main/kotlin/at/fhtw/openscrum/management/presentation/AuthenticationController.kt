package at.fhtw.openscrum.management.presentation

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(AuthenticationController.BASE_URL)
class AuthenticationController {
    companion object {
        const val BASE_URL = "/login"
        const val PATH_INDEX = "/"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun showLogin(): String = "login"
}
