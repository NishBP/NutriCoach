package com.fit2081.nishal34715231

import android.app.Application
import android.content.Context
import android.util.Log
import com.fit2081.nishal34715231.data.AppDatabase
import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.repository.NutriTrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Custom Application class for NutriCoach.
 * This class is instantiated when the application starts, before any Activity.
 * It's a good place for app-level initializations, like setting up the database
 * and loading initial data from CSV on the first launch.
 */
class NutriCoachApplication : Application() {

    // Using by lazy to initialize the database and repository only when they are first accessed.
    // This is a thread-safe way to perform lazy initialization.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: NutriTrackRepository by lazy {
        NutriTrackRepository(database.patientDao(), database.foodIntakeDataDao())
    }

    // A CoroutineScope for background tasks that live as long as the application.
    private val applicationScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val PREFS_NAME = "NutriCoachPrefs" // Changed from NutriTrackPrefs
        private const val KEY_CSV_LOADED = "csv_loaded"
        private const val TAG = "NutriCoachApplication" // Changed from NutriTrackApplication
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate called.")
        checkAndLoadCsvData()
    }

    /**
     * Checks if the CSV data has already been loaded. If not, it loads the data.
     * Uses SharedPreferences to store a flag indicating whether the CSV has been loaded.
     */
    private fun checkAndLoadCsvData() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val csvLoaded = prefs.getBoolean(KEY_CSV_LOADED, false)

        if (!csvLoaded) {
            Log.i(TAG, "CSV data not loaded yet. Attempting to load.")
            applicationScope.launch {
                // Check patient count in DB first, as an additional safeguard
                // This is useful if SharedPreferences got cleared but DB still has data.
                val patientCount = repository.getPatientCount()
                if (patientCount == 0) {
                    Log.i(TAG, "Database is empty. Proceeding with CSV load.")
                    loadPatientsFromCsv()
                    // After successful loading, set the flag in SharedPreferences
                    prefs.edit().putBoolean(KEY_CSV_LOADED, true).apply()
                    Log.i(TAG, "CSV data loaded successfully and flag set.")
                } else {
                    Log.i(TAG, "Database already contains $patientCount patients. Assuming CSV was loaded. Setting flag.")
                    // If DB has data but flag was false, set the flag to true to avoid re-checking.
                    prefs.edit().putBoolean(KEY_CSV_LOADED, true).apply()
                }
            }
        } else {
            Log.i(TAG, "CSV data already loaded (flag is true).")
        }
    }

    /**
     * Reads patient data from the 'user_data.csv' file in the assets folder
     * and inserts it into the Room database.
     */
    private suspend fun loadPatientsFromCsv() {
        val patients = mutableListOf<Patient>()
        try {
            // Open the CSV file from the assets folder
            assets.open("user_data.csv").use { inputStream ->
                InputStreamReader(inputStream).use { streamReader ->
                    BufferedReader(streamReader).use { reader ->
                        var line: String?
                        reader.readLine() // Skip header line

                        while (reader.readLine().also { line = it } != null) {
                            val tokens = line!!.split(",") // Split the line by comma
                            if (tokens.size >= 16) { // Ensure there are enough columns
                                val patient = Patient(
                                    userId = tokens[0].trim(),
                                    phoneNumber = tokens[1].trim(),
                                    name = tokens[2].trim(),
                                    sex = tokens[3].trim(),
                                    heifaTotalScore = tokens[4].toFloatOrNull(),
                                    fruitScore = tokens[5].toFloatOrNull(),
                                    vegScore = tokens[6].toFloatOrNull(),
                                    grainScore = tokens[7].toFloatOrNull(),
                                    dairyScore = tokens[8].toFloatOrNull(),
                                    meatFishPoultryScore = tokens[9].toFloatOrNull(),
                                    fatsOilsScore = tokens[10].toFloatOrNull(),
                                    waterScore = tokens[11].toFloatOrNull(),
                                    dietVarietyScore = tokens[12].toFloatOrNull(),
                                    addedSugarScore = tokens[13].toFloatOrNull(),
                                    alcoholScore = tokens[14].toFloatOrNull(),
                                    outsideHomeScore = tokens[15].toFloatOrNull()
                                )
                                patients.add(patient)
                            } else {
                                Log.w(TAG, "Skipping malformed CSV line: $line")
                            }
                        }
                    }
                }
            }
            if (patients.isNotEmpty()) {
                repository.insertAllPatients(patients) // Batch insert all parsed patients
                Log.i(TAG, "Successfully inserted ${patients.size} patients from CSV.")
            } else {
                Log.w(TAG, "No patients found in CSV or all lines were malformed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading patients from CSV", e)
        }
    }
}