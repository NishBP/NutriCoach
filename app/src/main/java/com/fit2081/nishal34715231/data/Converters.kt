package com.fit2081.nishal34715231.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters for Room to handle complex data types that Room doesn't support natively.
 * In this case, it converts a List<String> to a JSON String and vice-versa.
 */
class Converters {
    /**
     * Converts a JSON String to a List<String>.
     * Room will use this when reading from the database.
     * @param value The JSON string from the database.
     * @return A List<String> or null if the input string is null.
     */
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        if (value == null) {
            return null
        }
        // Define the type for Gson to deserialize into (List of Strings)
        val listType = object : TypeToken<List<String>>() {}.type
        // Use Gson to parse the JSON string back into a List<String>
        return Gson().fromJson(value, listType)
    }

    /**
     * Converts a List<String> to a JSON String.
     * Room will use this when writing to the database.
     * @param list The List<String> to convert.
     * @return A JSON string representation of the list, or null if the input list is null.
     */
    @TypeConverter
    fun fromList(list: List<String>?): String? {
        if (list == null) {
            return null
        }
        // Use Gson to convert the List<String> into a JSON string
        return Gson().toJson(list)
    }

    /**
     * Converts a Date to a Long (timestamp).
     * Room will use this when writing to the database.
     * @param date The Date to convert.
     * @return A Long representation of the date (milliseconds since epoch), or null if input is null.
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    /**
     * Converts a Long (timestamp) to a Date.
     * Room will use this when reading from the database.
     * @param value The timestamp in milliseconds since epoch.
     * @return A Date object, or null if input is null.
     */
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
}