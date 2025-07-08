package ru.sharov.imgtodoc.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.sharov.imgtodoc.domain.ImageToWordController
import java.awt.Desktop
import java.io.File

class ImagesViewModel {

    private val imageToWordController by lazy { ImageToWordController() }

    private val _filesFlow: MutableStateFlow<List<File>> = MutableStateFlow(emptyList())
    val filesFlow: StateFlow<List<File>> = _filesFlow.asStateFlow()

    var isShouldOpenFile: Boolean = false

    fun updateFiles(files: List<File>) {
        _filesFlow.update { files }
    }

    fun saveFile(file: String, format: OutputFormat) {
        println("save to $file")
        when(format) {
            OutputFormat.WORD -> imageToWordController.saveImagesToWord(filesFlow.value, file)
            OutputFormat.POWER_POINT -> imageToWordController.saveToPowerPoint(filesFlow.value, file)
        }

        if (isShouldOpenFile) {
            openWordFile(file)
        }
    }

    fun shouldOpenFile(it: Boolean) {
        isShouldOpenFile = it
    }

    private fun openWordFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(file) // Запустит файл в программе по умолчанию
                    } catch (e: Exception) {
                        println("Ошибка при открытии файла: ${e.message}")
                    }
                }
            }
        } else {
            println("Файл не найден: $filePath")
        }
    }

    enum class OutputFormat {
        WORD,
        POWER_POINT
    }
}
