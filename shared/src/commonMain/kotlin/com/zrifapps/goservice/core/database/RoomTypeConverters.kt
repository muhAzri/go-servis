package com.zrifapps.goservice.core.database

import androidx.room.TypeConverter

class RoomTypeConverters {

    @TypeConverter
    fun stringListToString(value: List<String>?): String? =
        value?.joinToString(separator = DELIMITER)

    @TypeConverter
    fun stringToStringList(value: String?): List<String>? =
        value?.split(DELIMITER)?.filter { it.isNotBlank() }

    private companion object {
        const val DELIMITER = "|"
    }
}
