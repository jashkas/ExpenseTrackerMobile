package com.example.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType, // INCOME или EXPENSE
    val category: String,
    val date: LocalDateTime,
    val notes: String? = null
) {
    enum class TransactionType {
        INCOME, EXPENSE
    }
}