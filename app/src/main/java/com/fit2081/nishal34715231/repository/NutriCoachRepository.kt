package com.fit2081.nishal34715231.repository

import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.data.PatientDao
import com.fit2081.nishal34715231.data.FoodIntakeData
import com.fit2081.nishal34715231.data.FoodIntakeDataDao
import com.fit2081.nishal34715231.data.NutriCoachTip
import com.fit2081.nishal34715231.data.NutriCoachTipDao
import kotlinx.coroutines.flow.Flow

class NutriCoachRepository(
    private val patientDao: PatientDao,
    private val foodIntakeDataDao: FoodIntakeDataDao,
    private val nutriCoachTipDao: NutriCoachTipDao
) {

    // Patient related operations
    fun getPatientById(userId: String): Flow<Patient?> = patientDao.getPatientById(userId)

    // New method to get patient for one-time reads (e.g., login validation)
    suspend fun getPatientByIdOnce(userId: String): Patient? = patientDao.getPatientByIdOnce(userId)

    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    // New method to get all patient UserIDs for dropdowns
    fun getAllPatientUserIds(): Flow<List<String>> = patientDao.getAllPatientUserIds()

    suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    suspend fun insertAllPatients(patients: List<Patient>) {
        patientDao.insertAllPatients(patients)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun deletePatientById(userId: String) {
        patientDao.deletePatientById(userId)
    }

    suspend fun getPatientCount(): Int = patientDao.getPatientCount()

    // FoodIntakeData related operations
    fun getFoodIntakeDataByUserId(patientUserId: String): Flow<FoodIntakeData?> {
        return foodIntakeDataDao.getFoodIntakeDataByUserId(patientUserId)
    }

    suspend fun getFoodIntakeDataByUserIdOnce(patientUserId: String): FoodIntakeData? {
        return foodIntakeDataDao.getFoodIntakeDataByUserIdOnce(patientUserId)
    }

    suspend fun insertFoodIntakeData(foodIntakeData: FoodIntakeData) {
        foodIntakeDataDao.insertFoodIntakeData(foodIntakeData)
    }

    suspend fun updateFoodIntakeData(foodIntakeData: FoodIntakeData) {
        foodIntakeDataDao.updateFoodIntakeData(foodIntakeData)
    }

    suspend fun deleteFoodIntakeDataByUserId(patientUserId: String) {
        foodIntakeDataDao.deleteFoodIntakeDataByUserId(patientUserId)
    }

    // NutriCoachTip related operations
    suspend fun insertTip(tip: NutriCoachTip): Long {
        return nutriCoachTipDao.insertTip(tip)
    }

    fun getTipsByUserId(userId: String): Flow<List<NutriCoachTip>> {
        return nutriCoachTipDao.getTipsByUserId(userId)
    }

    suspend fun getTipsByUserIdOnce(userId: String): List<NutriCoachTip> {
        return nutriCoachTipDao.getTipsByUserIdOnce(userId)
    }

    suspend fun deleteAllTipsByUserId(userId: String) {
        nutriCoachTipDao.deleteAllTipsByUserId(userId)
    }
}