package com.fit2081.nishal34715231.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.nishal34715231.data.AppDatabase
import com.fit2081.nishal34715231.data.FoodIntakeData
import com.fit2081.nishal34715231.repository.NutriTrackRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel for managing FoodIntakeData (questionnaire responses).
 * Interacts with NutriTrackRepository.
 *
 * @param application The application instance.
 */
class FoodIntakeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NutriTrackRepository

    // LiveData to hold the current user's food intake data.
    // Observed by the QuestionnaireScreen.
    private val _currentUserFoodIntake = MutableLiveData<FoodIntakeData?>()
    val currentUserFoodIntake: LiveData<FoodIntakeData?> = _currentUserFoodIntake

    init {
        val patientDao = AppDatabase.getDatabase(application).patientDao()
        val foodIntakeDataDao = AppDatabase.getDatabase(application).foodIntakeDataDao()
        repository = NutriTrackRepository(patientDao, foodIntakeDataDao)
    }

    /**
     * Loads food intake data for a given patient ID.
     * Updates the _currentUserFoodIntake LiveData.
     * This version uses the Flow and collects it, making it reactive.
     * @param patientUserId The ID of the patient.
     */
    fun loadFoodIntakeData(patientUserId: String) {
        viewModelScope.launch {
            repository.getFoodIntakeDataByUserId(patientUserId).collect { data ->
                _currentUserFoodIntake.postValue(data)
            }
        }
    }

    /**
     * Loads food intake data for a given patient ID for a one-time fetch.
     * This is useful if you don't need continuous observation, e.g., for pre-filling a form once.
     * @param patientUserId The ID of the patient.
     */
    suspend fun getFoodIntakeDataOnce(patientUserId: String): FoodIntakeData? {
        // This directly returns the data, useful for synchronous-like access within a coroutine
        return repository.getFoodIntakeDataByUserIdOnce(patientUserId)
    }


    /**
     * Saves (inserts or updates) food intake data.
     * It first tries to fetch existing data. If found, it updates; otherwise, it inserts.
     * @param foodIntakeData The FoodIntakeData to save.
     */
    fun saveFoodIntakeData(foodIntakeData: FoodIntakeData) = viewModelScope.launch {
        // Check if data for this user already exists to decide between insert and update.
        // The FoodIntakeDataDao.insertFoodIntakeData uses OnConflictStrategy.REPLACE,
        // so we can simplify this if the ID is managed correctly or if we always fetch first.
        // For simplicity with auto-generated ID, if foodIntakeData.id is 0, it's an insert.
        // If it has a valid ID (from a previous fetch), it's an update.

        // However, the primary key is `id`, not `patientUserId`.
        // A better approach for "insert or update" based on `patientUserId` would be:
        val existingData = repository.getFoodIntakeDataByUserIdOnce(foodIntakeData.patientUserId)
        if (existingData != null) {
            // If data exists, update it. Make sure to use the correct ID.
            repository.updateFoodIntakeData(foodIntakeData.copy(id = existingData.id))
        } else {
            // If no data exists, insert new data.
            repository.insertFoodIntakeData(foodIntakeData)
        }
    }
}