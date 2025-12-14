package com.example.myapplication12345678.data

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelExporter {

    fun exportStatsToCSV(context: Context, stats: List<UserStats>): File? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val fileName = "pystart_stats_${dateFormat.format(Date())}.csv"

            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            FileOutputStream(file).use { fos ->
                // BOM для корректного отображения кириллицы в Excel
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                // Заголовки
                val header = "Логин;Имя;Фамилия;Email;Завершённых курсов;Время обучения (мин)\n"
                fos.write(header.toByteArray(Charsets.UTF_8))

                // Данные
                stats.forEach { user ->
                    val row = "${user.login};${user.firstName};${user.lastName};${user.email};${user.completedCourses};${user.totalTimeMinutes}\n"
                    fos.write(row.toByteArray(Charsets.UTF_8))
                }
            }

            Toast.makeText(context, "Файл сохранён: $fileName", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка экспорта: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }

    fun shareCSVFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Поделиться статистикой"))
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка отправки: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
