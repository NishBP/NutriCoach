// File: app/src/main/java/com/fit2081/nishal34715231/NutriCoachApplication.kt
package com.fit2081.nishal34715231

import android.app.Application
import android.content.Context
import android.util.Log
import com.fit2081.nishal34715231.data.AppDatabase
import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.repository.NutriCoachRepository // Assuming you renamed this
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class NutriCoachApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: NutriCoachRepository by lazy {
        NutriCoachRepository(
            database.patientDao(),
            database.foodIntakeDataDao(),
            database.nutriCoachTipDao() // Added NutriCoachTipDao
        )
    }

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val PREFS_NAME = "NutriCoachPrefs"
        private const val KEY_CSV_LOADED = "csv_loaded"
        private const val TAG = "NutriCoachApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate called.")
        checkAndLoadCsvData()
    }

    private fun checkAndLoadCsvData() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val csvLoaded = prefs.getBoolean(KEY_CSV_LOADED, false)

        if (!csvLoaded) {
            Log.i(TAG, "CSV data not loaded yet. Attempting to load.")
            applicationScope.launch {
                val patientCount = repository.getPatientCount()
                if (patientCount == 0) {
                    Log.i(TAG, "Database is empty. Proceeding with CSV load.")
                    loadPatientsFromCsv()
                    prefs.edit().putBoolean(KEY_CSV_LOADED, true).apply()
                    Log.i(TAG, "CSV data loaded successfully and flag set.")
                } else {
                    Log.i(TAG, "Database already contains $patientCount patients. Assuming CSV was loaded. Setting flag.")
                    prefs.edit().putBoolean(KEY_CSV_LOADED, true).apply()
                }
            }
        } else {
            Log.i(TAG, "CSV data already loaded (flag is true).")
        }
    }

    private suspend fun loadPatientsFromCsv() {
        val patients = mutableListOf<Patient>()
        try {
            assets.open("user_data.csv").use { inputStream ->
                InputStreamReader(inputStream).use { streamReader ->
                    BufferedReader(streamReader).use { reader ->
                        val headerLine = reader.readLine()
                        if (headerLine == null) {
                            Log.e(TAG, "CSV file is empty or header is missing.")
                            return
                        }
                        val headers = headerLine.split(",").map { it.trim() }

                        // Get indices of necessary columns
                        val userIdIndex = headers.indexOf("User_ID")
                        val phoneNumberIndex = headers.indexOf("PhoneNumber")
                        val nameIndex = headers.indexOf("Name") // Assuming 'Name' column exists for initial load, or handle if it's only set on claim
                        val sexIndex = headers.indexOf("Sex")

                        // Define a helper to get score column index based on gender
                        fun getScoreIndex(baseNameMale: String, baseNameFemale: String, gender: String): Int {
                            return if (gender.equals("Male", ignoreCase = true)) {
                                headers.indexOf(baseNameMale)
                            } else {
                                headers.indexOf(baseNameFemale)
                            }
                        }

                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            val tokens = line!!.split(",").map { it.trim() }

                            if (userIdIndex == -1 || phoneNumberIndex == -1 || sexIndex == -1) {
                                Log.e(TAG, "Essential header columns (User_ID, PhoneNumber, Sex) not found in CSV.")
                                return // Stop processing if essential headers are missing
                            }
                            if (tokens.size <= maxOf(userIdIndex, phoneNumberIndex, sexIndex)) {
                                Log.w(TAG, "Skipping malformed CSV line (not enough tokens for essential fields): $line")
                                continue
                            }


                            val userId = tokens[userIdIndex]
                            val phoneNumber = tokens[phoneNumberIndex]
                            // Handle initial name: If CSV has a 'Name' column, use it. Otherwise, it will be null until claimed.
                            // For now, let's assume it might be missing or empty, and will be set on claim.
                            // If your CSV *always* has a name, you can use: val name = tokens[nameIndex]
                            val nameFromCsv = if (nameIndex != -1 && nameIndex < tokens.size) tokens[nameIndex] else null
                            val sex = tokens[sexIndex]

                            // Helper to parse float safely
                            fun parseFloatOrNull(index: Int): Float? {
                                return if (index != -1 && index < tokens.size && tokens[index].isNotEmpty()) {
                                    tokens[index].toFloatOrNull()
                                } else null
                            }

                            val patient = Patient(
                                userId = userId,
                                phoneNumber = phoneNumber,
                                name = nameFromCsv, // Will be updated on claim if null/empty
                                sex = sex,
                                password = null, // Password set on claim
                                isAccountClaimed = false, // Account not claimed initially
                                heifaTotalScore = parseFloatOrNull(getScoreIndex("HEIFAtotalscoreMale", "HEIFAtotalscoreFemale", sex)),
                                fruitScore = parseFloatOrNull(getScoreIndex("FruitHEIFAscoreMale", "FruitHEIFAscoreFemale", sex)),
                                vegScore = parseFloatOrNull(getScoreIndex("VegetablesHEIFAscoreMale", "VegetablesHEIFAscoreFemale", sex)), // Corrected from vegScore
                                grainScore = parseFloatOrNull(getScoreIndex("GrainsandcerealsHEIFAscoreMale", "GrainsandcerealsHEIFAscoreFemale", sex)), // Corrected from grainScore
                                dairyScore = parseFloatOrNull(getScoreIndex("DairyandalternativesHEIFAscoreMale", "DairyandalternativesHEIFAscoreFemale", sex)), // Corrected from dairyScore
                                meatFishPoultryScore = parseFloatOrNull(getScoreIndex("MeatandalternativesHEIFAscoreMale", "MeatandalternativesHEIFAscoreFemale", sex)),
                                fatsOilsScore = parseFloatOrNull(getScoreIndex("UnsaturatedFatHEIFAscoreMale", "UnsaturatedFatHEIFAscoreFemale", sex)), // Assuming this maps to fatsOilsScore
                                waterScore = parseFloatOrNull(getScoreIndex("WaterHEIFAscoreMale", "WaterHEIFAscoreFemale", sex)),
                                addedSugarScore = parseFloatOrNull(getScoreIndex("SugarHEIFAscoreMale", "SugarHEIFAscoreFemale", sex)), // Assuming this maps
                                alcoholScore = parseFloatOrNull(getScoreIndex("AlcoholHEIFAscoreMale", "AlcoholHEIFAscoreFemale", sex))
                            )
                            patients.add(patient)
                        }
                    }
                }
            }
            if (patients.isNotEmpty()) {
                repository.insertAllPatients(patients)
                Log.i(TAG, "Successfully inserted ${patients.size} patients from CSV.")
            } else {
                Log.w(TAG, "No patients found in CSV or all lines were malformed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading patients from CSV", e)
        }
    }
}