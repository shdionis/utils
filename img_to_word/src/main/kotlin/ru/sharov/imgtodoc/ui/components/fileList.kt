package ru.sharov.imgtodoc.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun FileList(files: List<File>) {
    LazyColumn(modifier = Modifier.padding(all = 8.dp)) {
        items(files) { file ->
            Text(file.absolutePath)
        }
    }
}