package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: ExpenseType, // INCOME or EXPENSE
    val category: String,
    val date: Date,
    val userId: String // Для привязки к пользователю
)

enum class ExpenseType {
    INCOME, EXPENSE
}