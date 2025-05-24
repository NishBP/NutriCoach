package com.fit2081.nishal34715231.repository

import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.data.PatientDao
import com.fit2081.nishal34715231.data.FoodIntakeData
import com.fit2081.nishal34715231.data.FoodIntakeDataDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to multiple data sources.
 * For this app, the primary data source is the Room database (via DAOs).
 * The Repository provides a clean API for data access to the rest of the application,
 * particularly the ViewModels.
 *
 * @param patientDao The DAO for patient data.
 * @param foodIntakeDataDao The DAO for food intake (questionnaire) data.
 */
class NutriTrackRepository(
    private val patientDao: PatientDao,
    private val foodIntakeDataDao: FoodIntakeDataDao
) {

    // Patient related operations

    /**
     * Retrieves a specific patient by their userId as a Flow.
     * The Flow allows for reactive updates if the patient data changes in the database.
     * @param userId The ID of the patient to retrieve.
     * @return A Flow emitting the Patient object, or null if not found.
     */
    fun getPatientById(userId: String): Flow<Patient?> = patientDao.getPatientById(userId)

    /**
     * Retrieves all patients from the database as a Flow.
     * @return A Flow emitting a list of all Patient objects.
     */
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    /**
     * Inserts a single patient into the database.
     * This is a suspend function and should be called from a coroutine.
     * @param patient The Patient object to insert.
     */
    suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    /**
     * Inserts a list of patients into the database.
     * Useful for batch operations like CSV import.
     * @param patients List of Patient objects to insert.
     */
    suspend fun insertAllPatients(patients: List<Patient>) {
        patientDao.insertAllPatients(patients)
    }

    /**
     * Updates an existing patient in the database.
     * @param patient The Patient object with updated information.
     */
    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    /**
     * Deletes a patient by their userId.
     * @param userId The ID of the patient to delete.
     */
    suspend fun deletePatientById(userId: String) {
        patientDao.deletePatientById(userId)
    }

    /**
     * Gets the total count of patients in the database.
     * @return The number of patients.
     */
    suspend fun getPatientCount(): Int = patientDao.getPatientCount()

    // FoodIntakeData related operations

    /**
     * Retrieves food intake data for a specific patient by their userId as a Flow.
     * @param patientUserId The ID of the patient.
     * @return A Flow emitting the FoodIntakeData object, or null if not found.
     */
    fun getFoodIntakeDataByUserId(patientUserId: String): Flow<FoodIntakeData?> {
        return foodIntakeDataDao.getFoodIntakeDataByUserId(patientUserId)
    }

    /**
     * Retrieves food intake data for a specific patient by their userId for a one-time fetch.
     * This is a suspend function for non-reactive data retrieval.
     * @param patientUserId The ID of the patient.
     * @return The FoodIntakeData object, or null if not found.
     */
    suspend fun getFoodIntakeDataByUserIdOnce(patientUserId: String): FoodIntakeData? {
        return foodIntakeDataDao.getFoodIntakeDataByUserIdOnce(patientUserId)
    }


    /**
     * Inserts food intake data into the database.
     * @param foodIntakeData The FoodIntakeData object to insert.
     */
    suspend fun insertFoodIntakeData(foodIntakeData: FoodIntakeData) {
        foodIntakeDataDao.insertFoodIntakeData(foodIntakeData)
    }

    /**
     * Updates existing food intake data in the database.
     * @param foodIntakeData The FoodIntakeData object with updated information.
     */
    suspend fun updateFoodIntakeData(foodIntakeData: FoodIntakeData) {
        foodIntakeDataDao.updateFoodIntakeData(foodIntakeData)
    }

    /**
     * Deletes food intake data for a specific patient by their userId.
     * @param patientUserId The ID of the patient whose food intake data should be deleted.
     */
    suspend fun deleteFoodIntakeDataByUserId(patientUserId: String) {
        foodIntakeDataDao.deleteFoodIntakeDataByUserId(patientUserId)
    }
}
