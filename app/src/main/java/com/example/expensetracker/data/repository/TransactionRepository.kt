package com.example.expensetracker.data.repository

import com.example.expensetracker.auth.JwtManager
import com.example.expensetracker.data.api.ApiService
import com.example.expensetracker.data.local.TransactionDao
import com.example.expensetracker.data.local.encryption.DataEncryptor
import com.example.expensetracker.data.model.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val apiService: ApiService,
    private val jwtManager: JwtManager,
    private val dataEncryptor: DataEncryptor,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun addTransaction(transaction: Transaction): Long {
        return withContext(ioDispatcher) {
            // Шифруем данные перед сохранением
            val encryptedTransaction = transaction.copy(
                title = dataEncryptor.encrypt(transaction.title),
                notes = transaction.notes?.let { dataEncryptor.encrypt(it) }
            )

            // Сохраняем локально
            val localId = transactionDao.insert(encryptedTransaction)

            // Пытаемся синхронизировать с сервером
            try {
                jwtManager.getToken()?.let { token ->
                    val serverTransaction = apiService.addTransaction(
                        token = "Bearer $token",
                        transaction = encryptedTransaction
                    ).body()

                    // Обновляем локальную запись с serverId
                    serverTransaction?.let {
                        transactionDao.update(
                            encryptedTransaction.copy(
                                id = localId,
                                serverId = serverTransaction.id
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок сети
                e.printStackTrace()
            }

            localId
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        withContext(ioDispatcher) {
            // Шифруем данные перед обновлением
            val encryptedTransaction = transaction.copy(
                title = dataEncryptor.encrypt(transaction.title),
                notes = transaction.notes?.let { dataEncryptor.encrypt(it) }
            )

            // Обновляем локально
            transactionDao.update(encryptedTransaction)

            // Пытаемся синхронизировать с сервером
            try {
                jwtManager.getToken()?.let { token ->
                    apiService.updateTransaction(
                        token = "Bearer $token",
                        id = transaction.serverId ?: return@let,
                        transaction = encryptedTransaction
                    )
                }
            } catch (e: Exception) {
                // Обработка ошибок сети
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        withContext(ioDispatcher) {
            // Удаляем локально
            transactionDao.delete(transaction)

            // Пытаемся синхронизировать с сервером
            try {
                jwtManager.getToken()?.let { token ->
                    transaction.serverId?.let { serverId ->
                        apiService.deleteTransaction(
                            token = "Bearer $token",
                            id = serverId
                        )
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок сети
                e.printStackTrace()
            }
        }
    }

    suspend fun getTransactionsBetweenDates(from: Date, to: Date): List<Transaction> {
        return withContext(ioDispatcher) {
            // Получаем зашифрованные данные из БД
            val encryptedTransactions = transactionDao.getTransactionsBetweenDates(from, to)

            // Расшифровываем данные
            encryptedTransactions.map { transaction ->
                transaction.copy(
                    title = dataEncryptor.decrypt(transaction.title),
                    notes = transaction.notes?.let { dataEncryptor.decrypt(it) }
                )
            }
        }
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return withContext(ioDispatcher) {
            // Получаем зашифрованную транзакцию
            val encryptedTransaction = transactionDao.getTransactionById(id) ?: return@withContext null

            // Расшифровываем данные
            encryptedTransaction.copy(
                title = dataEncryptor.decrypt(encryptedTransaction.title),
                notes = encryptedTransaction.notes?.let { dataEncryptor.decrypt(it) }
            )
        }
    }

    suspend fun searchTransactions(query: String): List<Transaction> {
        return withContext(ioDispatcher) {
            // Шифруем запрос для поиска в зашифрованных данных
            val encryptedQuery = dataEncryptor.encrypt(query)

            // Получаем зашифрованные результаты
            val encryptedResults = transactionDao.searchTransactions(encryptedQuery)

            // Расшифровываем результаты
            encryptedResults.map { transaction ->
                transaction.copy(
                    title = dataEncryptor.decrypt(transaction.title),
                    notes = transaction.notes?.let { dataEncryptor.decrypt(it) }
                )
            }
        }
    }

    suspend fun syncWithServer() {
        withContext(ioDispatcher) {
            try {
                jwtManager.getToken()?.let { token ->
                    // Получаем последние транзакции с сервера
                    val serverTransactions = apiService.getTransactions(
                        token = "Bearer $token",
                        from = 0,
                        to = System.currentTimeMillis()
                    ).body() ?: return@let

                    // Получаем локальные транзакции
                    val localTransactions = transactionDao.getTransactionsBetweenDates(
                        Date(0),
                        Date(System.currentTimeMillis())
                    )

                    // Синхронизация...
                    // (Здесь должна быть логика сравнения и обновления данных)
                }
            } catch (e: Exception) {
                // Обработка ошибок сети
                e.printStackTrace()
            }
        }
    }
}