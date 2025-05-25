package com.fit2081.nishal34715231.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPatients(patients: List<Patient>)

    @Query("SELECT * FROM patients WHERE userId = :userId")
    fun getPatientById(userId: String): Flow<Patient?>

    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatientByIdOnce(userId: String): Patient?

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT userId FROM patients ORDER BY userId ASC")
    fun getAllPatientUserIds(): Flow<List<String>>

    @Update
    suspend fun updatePatient(patient: Patient)

    @Query("DELETE FROM patients WHERE userId = :userId")
    suspend fun deletePatientById(userId: String)

    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int
}