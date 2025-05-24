package com.fit2081.nishal34715231.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.nishal34715231.data.AppDatabase
import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.repository.NutriTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Patient data.
 * It interacts with the NutriTrackRepository to fetch and manage patient information.
 * Extends AndroidViewModel to have access to the Application context, which is needed
 * to initialize the database and repository.
 *
 * @param application The application instance.
 */
class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NutriTrackRepository

    // LiveData to hold a single patient's details, typically fetched by ID.
    // This can be observed by the UI (e.g., InsightsScreen).
    // Using MutableLiveData internally and exposing LiveData externally is a common pattern.
    private val _selectedPatient = MutableLiveData<Patient?>()
    val selectedPatient: LiveData<Patient?> = _selectedPatient

    // LiveData to hold the list of all patients.
    // Could be used if you need to display a list of all patients somewhere.
    val allPatients: LiveData<List<Patient>>

    init {
        // Get instances of the DAOs from the AppDatabase
        val patientDao = AppDatabase.getDatabase(application).patientDao()
        val foodIntakeDataDao = AppDatabase.getDatabase(application).foodIntakeDataDao()
        // Initialize the repository with the DAOs
        repository = NutriTrackRepository(patientDao, foodIntakeDataDao)

        // Initialize allPatients by converting the Flow from the repository to LiveData
        allPatients = repository.getAllPatients().asLiveData()
    }

    /**
     * Fetches a patient by their ID and updates the _selectedPatient LiveData.
     * This is a suspend function and should be called from a coroutine,
     * typically launched from the UI or another ViewModel function.
     *
     * For reactive updates directly from Flow to UI (Compose), you might collect the Flow
     * in the Composable. If using LiveData for simplicity or with older UI patterns:
     *
     * @param userId The ID of the patient to fetch.
     */
    fun loadPatientById(userId: String) {
        viewModelScope.launch {
            // Collect the Flow from the repository and update LiveData
            // This approach makes selectedPatient LiveData update reactively.
            repository.getPatientById(userId).collect { patient ->
                _selectedPatient.postValue(patient)
            }
        }
    }

    /**
     * Inserts a new patient.
     * Operations that modify the database are launched in viewModelScope.
     * @param patient The patient to insert.
     */
    fun insertPatient(patient: Patient) = viewModelScope.launch {
        repository.insertPatient(patient)
    }

    /**
     * Updates an existing patient.
     * @param patient The patient to update.
     */
    fun updatePatient(patient: Patient) = viewModelScope.launch {
        repository.updatePatient(patient)
    }

    // Add other patient-related operations as needed (e.g., delete)
}