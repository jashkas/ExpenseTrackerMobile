package com.example.expensetracker.data.local

import androidx.room.*
import com.example.expensetracker.data.model.Transaction
import java.util.*

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    suspend fun getTransactionsBetweenDates(from: Date, to: Date): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    // Используем параметризованные запросы для защиты от SQL-инъекций
    @Query("SELECT * FROM transactions WHERE title LIKE :query OR notes LIKE :query ORDER BY date DESC")
    suspend fun searchTransactions(query: String): List<Transaction>
}