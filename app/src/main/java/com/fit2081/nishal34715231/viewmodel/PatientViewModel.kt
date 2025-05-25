// File: app/src/main/java/com/fit2081/nishal34715231/viewmodel/PatientViewModel.kt
package com.fit2081.nishal34715231.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.nishal34715231.data.AppDatabase
import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.repository.NutriCoachRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Enum to represent different states of login/registration process
sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val patient: Patient) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Idle : AuthResult() // Initial state or after a non-critical operation
}

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NutriCoachRepository

    // LiveData for all patient UserIDs (for the dropdown)
    val allPatientUserIds: LiveData<List<String>>

    // Add this: LiveData for all patients (for clinician dashboard)
    val allPatients: LiveData<List<Patient>>

    // LiveData to communicate authentication results to the UI
    private val _authResult = MutableLiveData<AuthResult>(AuthResult.Idle)
    val authResult: LiveData<AuthResult> = _authResult

    // LiveData to hold the currently logged-in/selected patient details for other screens
    private val _currentPatient = MutableLiveData<Patient?>()
    val currentPatient: LiveData<Patient?> = _currentPatient

    init {
        val patientDao = AppDatabase.getDatabase(application).patientDao()
        val foodIntakeDataDao = AppDatabase.getDatabase(application).foodIntakeDataDao()
        val nutriCoachTipDao = AppDatabase.getDatabase(application).nutriCoachTipDao() // Added this line
        repository = NutriCoachRepository(patientDao, foodIntakeDataDao, nutriCoachTipDao) // Updated this line

        // Fetch all patient UserIDs for the dropdown
        allPatientUserIds = repository.getAllPatientUserIds().asLiveData()

        // Add this: Fetch all patients for the clinician dashboard
        allPatients = repository.getAllPatients().asLiveData()
    }

    /**
     * Handles the account claim process.
     * Validates UserID and PhoneNumber against the database (pre-populated from CSV).
     * If valid, updates the patient record with name, password, and sets isAccountClaimed to true.
     */
    fun claimAccount(userId: String, phoneNumberInput: String, nameInput: String, passwordInput: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            val patient = repository.getPatientByIdOnce(userId)

            if (patient == null) {
                _authResult.postValue(AuthResult.Error("User ID not found."))
                return@launch
            }

            if (patient.isAccountClaimed) {
                _authResult.postValue(AuthResult.Error("Account already claimed. Please login."))
                return@launch
            }

            // Validate phone number from DB (which came from CSV)
            if (patient.phoneNumber != phoneNumberInput) {
                _authResult.postValue(AuthResult.Error("Invalid User ID or Phone Number."))
                return@launch
            }

            // Update patient details
            patient.name = nameInput
            patient.password = passwordInput // In a real app, hash the password
            patient.isAccountClaimed = true

            try {
                repository.updatePatient(patient)
                _currentPatient.postValue(patient) // Set the current patient
                _authResult.postValue(AuthResult.Success(patient))
            } catch (e: Exception) {
                _authResult.postValue(AuthResult.Error("Failed to claim account: ${e.message}"))
            }
        }
    }

    /**
     * Handles the login process.
     * Validates UserID and password against the database.
     */
    fun login(userId: String, passwordInput: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            val patient = repository.getPatientByIdOnce(userId)

            if (patient == null) {
                _authResult.postValue(AuthResult.Error("User ID not found."))
                return@launch
            }

            if (!patient.isAccountClaimed) {
                _authResult.postValue(AuthResult.Error("Account not claimed yet. Please register/claim your account."))
                return@launch
            }

            if (patient.password != passwordInput) { // In a real app, compare hashed passwords
                _authResult.postValue(AuthResult.Error("Invalid User ID or Password."))
                return@launch
            }
            _currentPatient.postValue(patient) // Set the current patient
            _authResult.postValue(AuthResult.Success(patient))
        }
    }

    /**
     * Fetches a patient by their ID to display on other screens (e.g., Insights, Settings).
     * This is an example of how other screens might get patient data.
     */
    fun loadCurrentPatientById(userId: String) {
        viewModelScope.launch {
            repository.getPatientById(userId).collect { patient ->
                _currentPatient.postValue(patient)
            }
        }
    }

    /**
     * Resets the authentication result to Idle.
     * Useful after an error message has been shown and acted upon.
     */
    fun resetAuthResult() {
        _authResult.value = AuthResult.Idle
    }

    // You might add a function here to check if a user (by userId) has completed the questionnaire
    // This would involve querying FoodIntakeData via the repository.
    // For now, this check can remain in the UI or be added later.
    suspend fun getPatientByIdOnce(userId: String): Patient? {
        return repository.getPatientByIdOnce(userId)
    }
}