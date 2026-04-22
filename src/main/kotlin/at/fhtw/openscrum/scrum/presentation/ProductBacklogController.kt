package at.fhtw.openscrum.scrum.presentation

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping(ProductBacklogController.BASE_URL)
class ProductBacklogController {
    companion object {
        const val BASE_URL = "/projects/{id}/backlog"
        const val PATH_INDEX = "/"
    }

    @GetMapping(value = ["", PATH_INDEX])
    fun index(
        @PathVariable id: UUID,
    ): String = "pages/list-product-backlog"
}
