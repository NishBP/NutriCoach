package com.fit2081.nishal34715231.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The main database class for the application.
 * It's an abstract class that extends RoomDatabase.
 * Room will generate the implementation for this class.
 *
 * @Database annotation specifies:
 * - entities: The list of entity classes that define the tables in the database.
 * - version: The version of the database schema. If you change the schema (e.g., add a table or column),
 * you MUST increment the version number and provide a migration strategy.
 * - exportSchema: Whether to export the schema to a JSON file in the project. It's good practice
 * to set this to true for version control of your schema, but for simplicity in
 * initial setup, it can be false. For production apps, set to true.
 *
 * @TypeConverters annotation registers the Converters class so Room knows how to handle
 * the List<String> to String conversion for the foodCategories field.
 */
@Database(
    entities = [Patient::class, FoodIntakeData::class], // List your entities here
    version = 1, // Start with version 1. Increment if you change the schema later.
    exportSchema = false // Set to true for production apps to export schema to a folder
)
@TypeConverters(Converters::class) // Register your type converters
abstract class AppDatabase : RoomDatabase() {

    /**
     * Abstract method to get the DAO for the Patient entity.
     * Room will generate the implementation.
     * @return An instance of PatientDao.
     */
    abstract fun patientDao(): PatientDao

    /**
     * Abstract method to get the DAO for the FoodIntakeData entity.
     * Room will generate the implementation.
     * @return An instance of FoodIntakeDataDao.
     */
    abstract fun foodIntakeDataDao(): FoodIntakeDataDao

    /**
     * Companion object to provide a singleton instance of the database.
     * This ensures that only one instance of the database is created throughout the app's lifecycle.
     * Using a singleton pattern is crucial for database access to avoid issues like multiple connections
     * or race conditions.
     */
    companion object {
        // @Volatile annotation ensures that the value of INSTANCE is always up-to-date and
        // the same to all execution threads. It means that changes made by one thread to INSTANCE
        // are visible to all other threads immediately.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton instance of the AppDatabase.
         * If the instance doesn't exist, it creates one in a thread-safe way.
         *
         * @param context The application context, needed to build the database.
         * @return The singleton AppDatabase instance.
         */
        // Corrected: "fun getDatabase" instead of "fungetDatabase"
        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's not null.
            // If it is null, then create the database in a synchronized block.
            // Synchronized block ensures that only one thread can execute this code at a time,
            // preventing multiple instances from being created if multiple threads try to access
            // the database at the same time.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context
                    AppDatabase::class.java,    // The AppDatabase class
                    "nutricoach_database"       // Name of the database file (changed to nutricoach)
                )
                    // .fallbackToDestructiveMigration() // If you increment version without a proper migration,
                    // this will delete and recreate the DB.
                    // NOT recommended for production without data loss consideration.
                    // .addCallback(MyDatabaseCallback(...)) // Optional: Add callbacks for DB creation/opening
                    .build() // Builds the database instance

                INSTANCE = instance // Assign the newly created database instance to INSTANCE
                instance // Return the instance
            }
        }
    }
}