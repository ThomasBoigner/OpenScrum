package at.fhtw.openscrum.scrum.presentation

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping(SprintController.BASE_URL)
class SprintController {
    companion object {
        const val BASE_URL = "/projects/{id}/sprints"
        const val PATH_INDEX = "/"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        @PathVariable id: UUID,
    ) = "pages/list-sprints"
}
