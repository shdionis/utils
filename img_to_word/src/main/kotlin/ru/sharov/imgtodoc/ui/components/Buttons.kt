package ru.sharov.imgtodoc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.sharov.imgtodoc.ui.ImagesViewModel
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

private val OPEN_FILES_EXTENSIONS = listOf(
    ".jpg",
    ".jpeg",
    ".png",
)

@Composable
fun OpenFilesButton(viewModel: ImagesViewModel) {
    Button(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), onClick = {
        FileDialog(null as Frame?, "Выбрать файл", FileDialog.LOAD).apply {
            filenameFilter = FilenameFilter { _, name -> OPEN_FILES_EXTENSIONS.any { name.endsWith(it) } }
            isMultipleMode = true // Включаем множественный выбор
            isVisible = true
            val files = files?.map { it } // Получаем List<File>
            files?.let { viewModel.updateFiles(it) }
        }

    }) {
        Text("Открыть файлы")
    }
}

@Composable
fun ConvertToWordButton(viewModel: ImagesViewModel) {
    Button(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), onClick = {
        FileDialog(null as Frame?, "Выбрать файл", FileDialog.SAVE).apply {
            isVisible = true
            file?.let {
                val targetFile = if (it.endsWith(".doc")) {
                    File(directory, it)
                } else {
                    File(directory, "$it.doc")
                }
                viewModel.saveFile(targetFile.absolutePath, ImagesViewModel.OutputFormat.WORD)
            }
        }

    }) {
        Text("Экспортировать в Word")
    }
}

@Composable
fun ConvertToPowerPointButton(viewModel: ImagesViewModel) {
    Button(modifier = Modifier.padding(all = 8.dp), onClick = {
        FileDialog(null as Frame?, "Выбрать файл", FileDialog.SAVE).apply {
            isVisible = true
            file?.let {
                val targetFile = if (it.endsWith(".ppt")) {
                    File(directory, it)
                } else {
                    File(directory, "$it.ppt")
                }
                viewModel.saveFile(targetFile.absolutePath, ImagesViewModel.OutputFormat.POWER_POINT)
            }
        }

    }) {
        Text("Экспортировать в Power Point")
    }
}

@Composable
fun LabeledCheckbox(text: String, viewModel: ImagesViewModel) {
    var checked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                checked = !checked
                viewModel.shouldOpenFile(checked)
            }
            .padding(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // Управляется через Row.clickable
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Blue,
                uncheckedColor = Color.Gray
            )
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}