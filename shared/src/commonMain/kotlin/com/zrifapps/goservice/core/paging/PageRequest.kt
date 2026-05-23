package com.zrifapps.goservice.core.paging

data class PageRequest(
    val offset: Int = 0,
    val limit: Int = DEFAULT_LIMIT,
) {
    init {
        require(offset >= 0) { "offset must be >= 0" }
        require(limit in 1..MAX_LIMIT) { "limit must be in 1..$MAX_LIMIT" }
    }

    companion object {
        const val DEFAULT_LIMIT: Int = 50
        const val MAX_LIMIT: Int = 200
        val FIRST: PageRequest = PageRequest()
    }
}

data class Page<T>(
    val items: List<T>,
    val total: Int,
    val request: PageRequest,
) {
    val hasMore: Boolean get() = request.offset + items.size < total
}
