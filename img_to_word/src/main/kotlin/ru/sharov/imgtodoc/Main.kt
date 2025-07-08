package ru.sharov.imgtodoc

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.sharov.imgtodoc.ui.screen.HomeScreen

@Preview
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Картинки в ворд") {
        HomeScreen()
    }
}
