package com.example.expensetracker.data.local

import androidx.room.*
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.ExpenseType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {

    // Базовые CRUD операции
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    // Получение всех расходов/доходов для пользователя
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpenses(userId: String): Flow<List<Expense>>

    // Сумма доходов
    @Query("SELECT SUM(amount) FROM expenses WHERE type = 'INCOME' AND userId = :userId")
    suspend fun getTotalIncome(userId: String): Double

    // Сумма расходов
    @Query("SELECT SUM(amount) FROM expenses WHERE type = 'EXPENSE' AND userId = :userId")
    suspend fun getTotalExpenses(userId: String): Double

    // 1. Получение расходов/доходов по категории
    @Query("SELECT * FROM expenses WHERE userId = :userId AND category = :category ORDER BY date DESC")
    fun getExpensesByCategory(userId: String, category: String): Flow<List<Expense>>

    // 2. Получение расходов/доходов за период
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Expense>>

    // 3. Сумма по категории
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND category = :category AND type = :type")
    suspend fun getSumByCategory(userId: String, category: String, type: ExpenseType): Double

    // 4. Получение уникальных категорий
    @Query("SELECT DISTINCT category FROM expenses WHERE userId = :userId AND type = :type")
    suspend fun getCategoriesByType(userId: String, type: ExpenseType): List<String>

    // 5. Получение последних N записей
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentExpenses(userId: String, limit: Int): Flow<List<Expense>>

    // 6. Поиск по описанию
    @Query("SELECT * FROM expenses WHERE userId = :userId AND title LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchExpenses(userId: String, query: String): Flow<List<Expense>>

    // Добавить проверку userId в @Delete, @Update для безопасности
    @Query("DELETE FROM expenses WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Int, userId: String)

    @Query("UPDATE expenses SET title = :title, amount = :amount, category = :category WHERE id = :id AND userId = :userId")
    suspend fun updateExpense(id: Int, userId: String, title: String, amount: Double, category: String)
}