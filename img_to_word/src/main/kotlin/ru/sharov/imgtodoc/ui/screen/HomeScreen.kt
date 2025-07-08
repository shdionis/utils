package ru.sharov.imgtodoc.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.sharov.imgtodoc.ui.ImagesViewModel
import ru.sharov.imgtodoc.ui.components.ConvertToPowerPointButton
import ru.sharov.imgtodoc.ui.components.ConvertToWordButton
import ru.sharov.imgtodoc.ui.components.FileList
import ru.sharov.imgtodoc.ui.components.LabeledCheckbox
import ru.sharov.imgtodoc.ui.components.OpenFilesButton

@Composable
@Preview
fun HomeScreen(viewModel: ImagesViewModel = remember { ImagesViewModel() }) {
    val files by viewModel.filesFlow.collectAsState()
    Column(modifier = Modifier.fillMaxWidth()) {
        OpenFilesButton(viewModel)
        Row {
            Column(modifier = Modifier.fillMaxWidth(0.5F)) {
                FileList(files)
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                ConvertToWordButton(viewModel)
                ConvertToPowerPointButton(viewModel)
                LabeledCheckbox("Открывать файл после сохранения", viewModel)

            }
        }
    }
}

