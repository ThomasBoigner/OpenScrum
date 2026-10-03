package at.fhtw.openscrum.management.domain.util

data class Page<T>(
    val content: MutableList<T>,
    val last: Boolean,
    var totalPages: Int,
    var totalElements: Long,
    var first: Boolean,
    var size: Int,
    var number: Int,
    var numberOfElements: Int,
    var empty: Boolean,
)
