package com.example.expensetracker.data.local.encryption

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets

class DataEncryptor(private val context: Context) {
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    fun encrypt(data: String): String {
        val file = File(context.filesDir, "secure_data_encrypted.tmp")

        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().bufferedWriter(StandardCharsets.UTF_8).use { writer ->
            writer.write(data)
        }

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }

    fun decrypt(encryptedData: String): String {
        val file = File(context.filesDir, "secure_data_encrypted.tmp")
        file.writeBytes(Base64.decode(encryptedData, Base64.NO_WRAP))

        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return encryptedFile.openFileInput().bufferedReader(StandardCharsets.UTF_8).use { reader ->
            reader.readText()
        }
    }
}