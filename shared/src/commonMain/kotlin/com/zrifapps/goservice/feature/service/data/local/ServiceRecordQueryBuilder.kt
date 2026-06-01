package com.zrifapps.goservice.feature.service.data.local

import androidx.room.RoomRawQuery
import com.zrifapps.goservice.core.paging.PageRequest
import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort

/**
 * Builds the dynamic SQL for [ServiceRecordDao.queryPaged]/[ServiceRecordDao.queryCount].
 *
 * Filtering, sorting and paging are pushed down to SQLite (LIMIT/OFFSET) so a large
 * history table is never loaded into memory at once. The WHERE clause mirrors the
 * in-memory `applyFilter`/`applySort` used by the observe* flows.
 */
internal object ServiceRecordQueryBuilder {

    private const val LIKE_ESCAPE = '\\'

    /** Page of rows: same filter, ordered, then LIMIT/OFFSET. */
    fun page(filter: ServiceFilter, sort: ServiceSort, page: PageRequest): RoomRawQuery {
        val (where, args) = where(filter)
        val sql = buildString {
            append("SELECT * FROM service_records WHERE ")
            append(where)
            append(" ORDER BY ")
            append(orderBy(sort))
            append(" LIMIT ? OFFSET ?")
        }
        return rawQuery(sql, args + page.limit.toLong() + page.offset.toLong())
    }

    /** Total row count for the same filter, ignoring sort and paging. */
    fun count(filter: ServiceFilter): RoomRawQuery {
        val (where, args) = where(filter)
        return rawQuery("SELECT COUNT(*) FROM service_records WHERE $where", args)
    }

    /** Sum of cost over the whole filtered set (not just one page); 0 when empty. */
    fun sumCost(filter: ServiceFilter): RoomRawQuery {
        val (where, args) = where(filter)
        return rawQuery("SELECT COALESCE(SUM(cost_idr), 0) FROM service_records WHERE $where", args)
    }

    private fun where(filter: ServiceFilter): Pair<String, List<Any>> {
        val clauses = mutableListOf("sync_deleted_at IS NULL")
        val args = mutableListOf<Any>()

        if (filter.vehicleIds.isNotEmpty()) {
            clauses += "vehicle_id IN (${placeholders(filter.vehicleIds.size)})"
            args.addAll(filter.vehicleIds)
        }
        if (filter.serviceTypes.isNotEmpty()) {
            clauses += "service_type IN (${placeholders(filter.serviceTypes.size)})"
            args.addAll(filter.serviceTypes.map { it.key })
        }
        if (filter.componentIds.isNotEmpty()) {
            clauses += "EXISTS (SELECT 1 FROM service_record_component_xref x " +
                "WHERE x.service_record_id = service_records.id " +
                "AND x.component_id IN (${placeholders(filter.componentIds.size)}))"
            args.addAll(filter.componentIds)
        }
        filter.dateRange?.let {
            clauses += "service_date BETWEEN ? AND ?"
            args.add(it.start)
            args.add(it.endInclusive)
        }
        filter.minCost?.let {
            clauses += "cost_idr >= ?"
            args.add(it.amountIdr)
        }
        filter.maxCost?.let {
            clauses += "cost_idr <= ?"
            args.add(it.amountIdr)
        }
        filter.query?.takeIf { it.isNotBlank() }?.let {
            clauses += "(workshop LIKE ? ESCAPE '$LIKE_ESCAPE' OR note LIKE ? ESCAPE '$LIKE_ESCAPE')"
            val pattern = "%" + escapeLike(it) + "%"
            args.add(pattern)
            args.add(pattern)
        }
        return clauses.joinToString(" AND ") to args
    }

    // Stable tiebreaker on id keeps offset paging from skipping/duplicating rows that share a sort key.
    private fun orderBy(sort: ServiceSort): String {
        val column = when (sort) {
            ServiceSort.DateDesc -> "service_date DESC"
            ServiceSort.DateAsc -> "service_date ASC"
            ServiceSort.OdometerDesc -> "odometer_km DESC"
            ServiceSort.OdometerAsc -> "odometer_km ASC"
            ServiceSort.CostDesc -> "cost_idr DESC"
            ServiceSort.CostAsc -> "cost_idr ASC"
        }
        return "$column, id ASC"
    }

    private fun placeholders(count: Int): String = List(count) { "?" }.joinToString(", ")

    // Treat the user's text literally: escape LIKE wildcards so they match `contains`, not glob.
    private fun escapeLike(input: String): String = buildString {
        for (c in input) {
            when (c) {
                LIKE_ESCAPE, '%', '_' -> append(LIKE_ESCAPE)
            }
            append(c)
        }
    }

    private fun rawQuery(sql: String, args: List<Any>): RoomRawQuery =
        RoomRawQuery(sql) { statement ->
            args.forEachIndexed { index, arg ->
                val position = index + 1
                when (arg) {
                    is Long -> statement.bindLong(position, arg)
                    is Int -> statement.bindLong(position, arg.toLong())
                    is String -> statement.bindText(position, arg)
                    else -> error("Unsupported bind arg type: ${arg::class.simpleName}")
                }
            }
        }
}
