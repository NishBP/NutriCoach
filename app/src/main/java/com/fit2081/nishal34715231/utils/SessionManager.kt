package com.fit2081.nishal34715231.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Centralized session management for consistent user authentication handling
 * Think of this as the hotel's front desk - manages check-ins, check-outs, and key cards
 */
object SessionManager {

    private const val PREFS_NAME = "NutriCoachPrefs"
    private const val KEY_LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID"
    private const val TAG = "SessionManager"

    /**
     * Save user session (like giving someone a hotel key card)
     */
    fun saveUserSession(context: Context, userId: String) {
        try {
            val prefs = getPrefs(context)
            prefs.edit()
                .putString(KEY_LOGGED_IN_USER_ID, userId)
                .apply()
            Log.i(TAG, "User session saved for ID: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user session", e)
        }
    }

    /**
     * Get current logged-in user ID (like checking if someone has a valid key card)
     */
    fun getCurrentUserId(context: Context): String? {
        return try {
            val prefs = getPrefs(context)
            val userId = prefs.getString(KEY_LOGGED_IN_USER_ID, null)
            Log.d(TAG, "Current user ID: ${userId ?: "none"}")
            userId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current user ID", e)
            null
        }
    }

    /**
     * Check if user is logged in (like checking if someone has a valid key card)
     */
    fun isUserLoggedIn(context: Context): Boolean {
        return getCurrentUserId(context) != null
    }

    /**
     * Clear user session (like checking out of hotel - return the key card)
     */
    fun clearUserSession(context: Context) {
        try {
            val prefs = getPrefs(context)
            prefs.edit()
                .remove(KEY_LOGGED_IN_USER_ID)
                .apply()
            Log.i(TAG, "User session cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear user session", e)
        }
    }

    /**
     * Update user session with new user ID (like updating hotel registration)
     */
    fun updateUserSession(context: Context, newUserId: String) {
        saveUserSession(context, newUserId)
    }

    /**
     * Get SharedPreferences instance
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Clear all app preferences (nuclear option - clear everything)
     * Use this only in extreme cases like data corruption
     */
    fun clearAllPreferences(context: Context) {
        try {
            val prefs = getPrefs(context)
            prefs.edit().clear().apply()
            Log.w(TAG, "All preferences cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all preferences", e)
        }
    }
}