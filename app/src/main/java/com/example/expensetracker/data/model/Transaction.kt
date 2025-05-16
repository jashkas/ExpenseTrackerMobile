package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serverId: Long? = null,  // Добавлено для синхронизации
    val title: String,
    val amount: Double,
    val type: String, // "income" or "expense"
    val category: String,
    val date: Date,
    val notes: String? = null,
    val isEncrypted: Boolean = false
)