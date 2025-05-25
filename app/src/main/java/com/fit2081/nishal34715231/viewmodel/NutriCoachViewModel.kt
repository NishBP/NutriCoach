package com.fit2081.nishal34715231.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit2081.nishal34715231.BuildConfig
import com.fit2081.nishal34715231.api.FruitApiService
import com.fit2081.nishal34715231.data.AppDatabase
import com.fit2081.nishal34715231.data.Fruit
import com.fit2081.nishal34715231.data.NutriCoachTip
import com.fit2081.nishal34715231.data.Patient
import com.fit2081.nishal34715231.repository.NutriCoachRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

enum class UiState {
    IDLE, LOADING, SUCCESS, ERROR
}

class NutriCoachViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NutriCoachRepository
    private val fruitApiService = FruitApiService()

    // Gemini generative model
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    // LiveData for current patient
    private val _currentPatient = MutableLiveData<Patient?>()
    val currentPatient: LiveData<Patient?> = _currentPatient

    // State for fruit search
    private val _fruitSearchState = MutableStateFlow(UiState.IDLE)
    val fruitSearchState: StateFlow<UiState> = _fruitSearchState.asStateFlow()

    private val _searchedFruit = MutableLiveData<Fruit?>()
    val searchedFruit: LiveData<Fruit?> = _searchedFruit

    // State for GenAI responses
    private val _aiResponseState = MutableStateFlow(UiState.IDLE)
    val aiResponseState: StateFlow<UiState> = _aiResponseState.asStateFlow()

    private val _currentAiResponse = MutableLiveData<String?>()
    val currentAiResponse: LiveData<String?> = _currentAiResponse

    private val _allUserTips = MutableLiveData<List<NutriCoachTip>>(emptyList())
    val allUserTips: LiveData<List<NutriCoachTip>> = _allUserTips

    private val _aiResponseType = MutableLiveData<String>()
    val aiResponseType: LiveData<String> = _aiResponseType

    init {
        val patientDao = AppDatabase.getDatabase(application).patientDao()
        val foodIntakeDataDao = AppDatabase.getDatabase(application).foodIntakeDataDao()
        val nutriCoachTipDao = AppDatabase.getDatabase(application).nutriCoachTipDao()
        repository = NutriCoachRepository(patientDao, foodIntakeDataDao, nutriCoachTipDao)
    }

    fun loadPatientById(userId: String) {
        viewModelScope.launch {
            repository.getPatientById(userId).collect { patient ->
                _currentPatient.postValue(patient)
            }
        }
    }

    fun searchFruitByName(fruitName: String) {
        viewModelScope.launch {
            _fruitSearchState.value = UiState.LOADING
            _searchedFruit.postValue(null)

            try {
                val result = fruitApiService.getFruitByName(fruitName)

                if (result.isSuccess) {
                    _searchedFruit.postValue(result.getOrNull())
                    _fruitSearchState.value = UiState.SUCCESS
                } else {
                    _fruitSearchState.value = UiState.ERROR
                    Log.e("NutriCoachViewModel", "Error searching fruit: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _fruitSearchState.value = UiState.ERROR
                Log.e("NutriCoachViewModel", "Exception searching fruit", e)
            }
        }
    }

    fun generateMotivationalTip(userId: String) {
        generateAiResponse(userId, "MOTIVATION")
    }

    fun generateFoodTip(userId: String) {
        generateAiResponse(userId, "FOOD_TIP")
    }

    private fun generateAiResponse(userId: String, tipType: String) {
        viewModelScope.launch {
            _aiResponseState.value = UiState.LOADING
            _aiResponseType.postValue(tipType)

            val patient = repository.getPatientByIdOnce(userId)
            if (patient == null) {
                _aiResponseState.value = UiState.ERROR
                return@launch
            }

            try {
                // Create a prompt based on patient data and tip type
                val prompt = buildPrompt(patient, tipType)

                // Generate response from Gemini
                val response = generativeModel.generateContent(prompt).text

                if (response != null) {
                    // Save the response to the database
                    val tip = NutriCoachTip(
                        userId = userId,
                        tipContent = response,
                        tipType = tipType
                    )
                    repository.insertTip(tip)

                    // Update the UI
                    _currentAiResponse.postValue(response)
                    _aiResponseState.value = UiState.SUCCESS

                    // Reload all tips
                    loadAllTipsForUser(userId)
                } else {
                    _aiResponseState.value = UiState.ERROR
                }
            } catch (e: Exception) {
                Log.e("NutriCoachViewModel", "Error generating AI response", e)
                _aiResponseState.value = UiState.ERROR
            }
        }
    }

    private fun buildPrompt(patient: Patient, tipType: String): String {
        return when (tipType) {
            "MOTIVATION" -> {
                """
                Generate a motivational message related to nutrition and health for a person with the following nutritional scores:
                
                Total HEIFA Score: ${patient.heifaTotalScore ?: "Unknown"}
                Fruit Score: ${patient.fruitScore ?: "Unknown"} (out of 10)
                Vegetable Score: ${patient.vegScore ?: "Unknown"} (out of 10)
                Grain Score: ${patient.grainScore ?: "Unknown"} (out of 10)
                Dairy Score: ${patient.dairyScore ?: "Unknown"} (out of 10)
                Meat/Fish/Poultry Score: ${patient.meatFishPoultryScore ?: "Unknown"} (out of 10)
                Fats/Oils Score: ${patient.fatsOilsScore ?: "Unknown"} (out of 10)
                Water Score: ${patient.waterScore ?: "Unknown"} (out of 10)
                Added Sugar Score: ${patient.addedSugarScore ?: "Unknown"} (out of 10)
                Alcohol Score: ${patient.alcoholScore ?: "Unknown"} (out of 10)
                
                The message should be positive, encouraging, and specific to their nutritional profile.
                Keep it under 3 sentences and make it personalized.
                """
            }
            "FOOD_TIP" -> {
                """
                Generate a fun, interesting food tip or nutrition fact that would be helpful for a person with the following nutritional scores:
                
                Total HEIFA Score: ${patient.heifaTotalScore ?: "Unknown"}
                Fruit Score: ${patient.fruitScore ?: "Unknown"} (out of 10)
                Vegetable Score: ${patient.vegScore ?: "Unknown"} (out of 10)
                Grain Score: ${patient.grainScore ?: "Unknown"} (out of 10)
                Dairy Score: ${patient.dairyScore ?: "Unknown"} (out of 10)
                Meat/Fish/Poultry Score: ${patient.meatFishPoultryScore ?: "Unknown"} (out of 10)
                Fats/Oils Score: ${patient.fatsOilsScore ?: "Unknown"} (out of 10)
                Water Score: ${patient.waterScore ?: "Unknown"} (out of 10)
                Added Sugar Score: ${patient.addedSugarScore ?: "Unknown"} (out of 10)
                Alcohol Score: ${patient.alcoholScore ?: "Unknown"} (out of 10)
                
                Focus on their lowest scoring areas. Make it practical, specific, and easy to implement.
                Keep it under 3 sentences and make it sound fun.
                """
            }
            else -> "Generate a helpful nutrition tip."
        }
    }

    fun clearCurrentAiResponse() {
        _currentAiResponse.postValue(null)
    }

    fun loadAllTipsForUser(userId: String) {
        viewModelScope.launch {
            try {
                repository.getTipsByUserId(userId).collect { tips ->
                    _allUserTips.postValue(tips)
                }
            } catch (e: Exception) {
                Log.e("NutriCoachViewModel", "Error loading tips", e)
            }
        }
    }
}