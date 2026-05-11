package at.fhtw.openscrum.management.presentation

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(ErrorController.BASE_URL)
class ErrorController {
    companion object {
        const val BASE_URL = "/error"
        const val ROUTE_BAD_REQUEST = "/400"
        const val ROUTE_ACCESS_DENIED = "/403"
        const val ROUTE_NOT_FOUND = "/404"
        const val ROUTE_INTERNAL_SERVER_ERROR = "/500"
    }

    @GetMapping(value = [ROUTE_BAD_REQUEST])
    fun showBadRequestPage(): String = "error/400"

    @GetMapping(value = [ROUTE_ACCESS_DENIED])
    fun showAccessDeniedPage(): String = "error/403"

    @GetMapping(value = [ROUTE_NOT_FOUND])
    fun showNotFoundPage(): String = "error/404"

    @GetMapping(value = [ROUTE_INTERNAL_SERVER_ERROR])
    fun showInternalServerErrorPage(): String = "error/500"
}
