package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItem
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class Sprint(
    val id: Long? = null,
    val sprintId: SprintId,
    val sprintName: String,
    val startDate: LocalDate = LocalDate.now(),
    endDate: LocalDate,
    status: SprintStatus = SprintStatus.NOT_PLANNED,
    sprintGoal: String? = null,
    val sprintBacklogItems: MutableSet<SprintBacklogItem> = mutableSetOf(),
    val sprintCanceledEvents: MutableList<SprintCanceled> = mutableListOf(),
    val sprintCompletedEvents: MutableList<SprintCompleted> = mutableListOf(),
) {
    constructor(
        sprintId: SprintId,
        sprintNumber: Int,
        startDate: LocalDate = LocalDate.now(),
        sprintLength: Long,
    ) : this(
        sprintId = sprintId,
        sprintName = "Sprint $sprintNumber",
        startDate = startDate,
        endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).plusWeeks(sprintLength).minusDays(1),
    )

    var endDate: LocalDate = endDate
        private set

    var status: SprintStatus = status
        private set

    var sprintGoal: String? = sprintGoal
        private set

    fun planSprint(
        scrumMaster: ScrumMaster?,
        sprintGoal: String,
        productBacklogItems: Set<ProductBacklogItem>,
    ) {
        require(sprintGoal.isNotBlank()) { "Sprint goal cannot be blank" }
        require(productBacklogItems.isNotEmpty()) { "Sprint backlog cannot be empty" }
        require(scrumMaster?.teamMemberId?.projectId == this.sprintId.projectId) {
            "You are not the scrum master of this project"
        }
        require(!status.isPlanned) { "The sprint cannot be planned" }
        require(productBacklogItems.all { it.productBacklogItemId.projectId == sprintId.projectId }) {
            "Cannot commit product backlog items of another project to this sprint"
        }

        this.sprintGoal = sprintGoal
        this.status = SprintStatus.IN_PROGRESS

        productBacklogItems.forEach { pbi ->
            sprintBacklogItems.add(
                SprintBacklogItem(
                    sprintBacklogItemId =
                        SprintBacklogItemId(
                            pbi.productBacklogItemId.projectId,
                            sprintId.sprintId,
                            pbi.productBacklogItemId.productBacklogItemId,
                        ),
                    title = pbi.title,
                    description = pbi.description,
                ),
            )
        }
    }

    fun moveSprintBacklogItem(
        sprintBacklogItemId: SprintBacklogItemId,
        moveDirection: MoveDirection,
        developer: Developer?,
    ): SprintBacklogItem {
        require(!status.isFinished) {
            "Sprint backlog items can only be moved if sprint is in progress"
        }
        require(developer?.teamMemberId?.projectId == this.sprintId.projectId) {
            "You are not a developer of this project"
        }
        val item =
            sprintBacklogItems.find { it.sprintBacklogItemId == sprintBacklogItemId } ?: throw IllegalArgumentException(
                "Could not find backlog item with sprintBacklogItemId=$sprintBacklogItemId",
            )
        item.move(moveDirection, developer.teamMemberId)
        return item
    }

    fun deleteSprintBacklogItem(productBackLogItemId: ProductBacklogItemId) {
        sprintBacklogItems.removeIf { it.sprintBacklogItemId.productBacklogItemId == productBackLogItemId.productBacklogItemId }
    }

    fun cancelSprint(productOwner: ProductOwner?) {
        require(!status.isFinished) {
            "Sprint can only be cancelled when its in progress or not planned"
        }
        require(productOwner?.teamMemberId?.projectId == this.sprintId.projectId) {
            "You are not the product owner of this project"
        }
        this.status = SprintStatus.CANCELLED

        sprintBacklogItems
            .forEach { it.uncommitFromSprint() }

        sprintCanceledEvents.add(SprintCanceled(sprintId = sprintId))
    }

    fun completeSprint() {
        require(!status.isFinished) {
            "Sprint can only be completed when its in progress or not planned"
        }
        this.status = SprintStatus.COMPLETED

        sprintBacklogItems.forEach { it.uncommitFromSprint() }

        sprintCompletedEvents.add(SprintCompleted(sprintId = sprintId))
    }

    fun getSprintBacklogItems(status: SprintBacklogItemStatus): List<SprintBacklogItem> = sprintBacklogItems.filter { it.status == status }

    override fun toString(): String =
        "Sprint(sprintBacklogItems=$sprintBacklogItems, startDate=$startDate, sprintName='$sprintName', sprintId=$sprintId)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sprint

        return sprintId == other.sprintId
    }

    override fun hashCode(): Int = sprintId.hashCode()
}
