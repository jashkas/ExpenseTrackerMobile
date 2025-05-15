package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAllExpenses(userId: String): Flow<List<Expense>> = expenseDao.getAllExpenses(userId)

    suspend fun insertExpense(expense: Expense) = expenseDao.insert(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)

    suspend fun getTotalIncome(userId: String): Double = expenseDao.getTotalIncome(userId)

    suspend fun getTotalExpenses(userId: String): Double = expenseDao.getTotalExpenses(userId)
}