package com.example.expensetracker.data.api

import com.example.expensetracker.data.model.Transaction
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): Response<List<Transaction>>

    @POST("transactions")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Body transaction: Transaction
    ): Response<Transaction>

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body transaction: Transaction
    ): Response<Transaction>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ResponseBody>
}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)