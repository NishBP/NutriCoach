package com.fit2081.nishal34715231.api

import com.fit2081.nishal34715231.data.Fruit
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FruitApiService {
    private val baseUrl = "https://www.fruityvice.com/api/fruit"
    private val gson = Gson()

    // Get fruit details by name
    suspend fun getFruitByName(fruitName: String): Result<Fruit> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$baseUrl/${fruitName.trim().lowercase()}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val fruit = gson.fromJson(response, Fruit::class.java)
                    Result.success(fruit)
                } else {
                    Result.failure(Exception("HTTP Error: $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Get all fruits
    suspend fun getAllFruits(): Result<List<Fruit>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$baseUrl/all")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val fruitArray = gson.fromJson(response, Array<Fruit>::class.java)
                    Result.success(fruitArray.toList())
                } else {
                    Result.failure(Exception("HTTP Error: $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}