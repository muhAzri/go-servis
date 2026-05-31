package com.zrifapps.goservice.feature.backup.domain.model

enum class BackupSection(val key: String) {
    Vehicles("vehicles"),
    Services("services"),
    Reminders("reminders"),
    Components("components");

    companion object {
        fun fromKey(key: String): BackupSection? = entries.firstOrNull { it.key == key }
        val ALL: Set<BackupSection> = entries.toSet()
    }
}

enum class BackupRange(val key: String, val days: Long?) {
    All("all", null),
    Month("month", 31L),
    ThreeMonths("3m", 92L),
    OneYear("1y", 366L);

    companion object {
        fun fromKey(key: String): BackupRange = entries.firstOrNull { it.key == key } ?: All
    }
}

data class BackupCounts(
    val vehicles: Int = 0,
    val services: Int = 0,
    val reminders: Int = 0,
    val components: Int = 0,
)

data class BackupExport(
    val filename: String,
    val content: String,
    val counts: BackupCounts,
)

data class BackupImportResult(
    val vehicles: Int = 0,
    val services: Int = 0,
    val reminders: Int = 0,
    val components: Int = 0,
    val skipped: Int = 0,
    val malformed: Boolean = false,
) {
    val imported: Int get() = vehicles + services + reminders + components
}
