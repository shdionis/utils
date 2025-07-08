package ru.sharov.imgtodoc.domain

import org.apache.poi.hslf.usermodel.HSLFSlide
import org.apache.poi.hslf.usermodel.HSLFSlideShow
import org.apache.poi.hslf.usermodel.HSLFTextBox
import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.sl.usermodel.TextParagraph
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import ru.sharov.imgtodoc.domain.exceptions.WrongFileException
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import javax.imageio.ImageIO


class ImageToWordController {
    fun saveImagesToWord(files: List<File>, outputFile: String) {
        files.forEach {
            if (!it.exists()) {
                throw FileNotFoundException()
            }
        }
        val imagePaths = files.map {
            it.absolutePath
        }

        try {
            XWPFDocument().use { doc ->
                for (imgPath in imagePaths) {
                    // Получаем имя файла без расширения
                    val fileName: String = File(imgPath).getName()
                    val caption = fileName.replaceFirst("[.][^.]+$".toRegex(), "")

                    val imageFileDims = getImageDimensions(File(imgPath))
                    if (imageFileDims == null) {
                        throw WrongFileException("Не картика!")
                    }
                    val outputHeight =
                        if (imageFileDims.isVertical()) DEFAULT_HEIGHT_OUTPUT_WORD else imageFileDims.second.toDouble() * (DEFAULT_WIDTH_OUTPUT_WORD / imageFileDims.first)
                    val outputWidth =
                        if (imageFileDims.isVertical()) imageFileDims.first.toDouble() * (DEFAULT_HEIGHT_OUTPUT_WORD / imageFileDims.second) else DEFAULT_WIDTH_OUTPUT_WORD
                    // Добавляем абзац с изображением
                    val paragraph = doc.createParagraph()
                    paragraph.alignment = ParagraphAlignment.CENTER
                    val run = paragraph.createRun()
                    run.addPicture(
                        FileInputStream(imgPath),
                        getPictureTypeWord(imgPath),
                        imgPath,
                        Units.toEMU(outputWidth),  // Ширина (можно менять)
                        Units.toEMU(outputHeight) // Высота
                    )

                    // Добавляем подпись
                    val captionPara = doc.createParagraph()
                    captionPara.alignment = ParagraphAlignment.CENTER
                    val captionRun = captionPara.createRun()
                    captionRun.setText(caption)
                    captionRun.isBold = true
                }
                FileOutputStream(outputFile).use { out ->
                    doc.write(out)
                }
                println("Generated: $outputFile")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun saveToPowerPoint(files: List<File>, outputFile: String) {
        files.forEach {
            if (!it.exists()) {
                throw FileNotFoundException()
            }
        }

        try {
            HSLFSlideShow().use { ppt ->

                val size = Dimension(SLIDE_WIDTH_PPT, SLIDE_HEIGHT_PPT) // 960x540 пикселей (для 16:9)
                ppt.pageSize = size
                files.forEach { img ->

                    val caption = img.name.replaceFirst("[.][^.]+$".toRegex(), "")
                    val slide: HSLFSlide = ppt.createSlide()

                    val title: HSLFTextBox = slide.createTextBox()
                    title.setAnchor(Rectangle(25, 25, 900, 100)) // Позиция и размер

                    val paragraph = title.textParagraphs.first()
                    val run = paragraph.textRuns.first()
                    run.setText(caption)
                    run.setFontSize(40.0) // Размер 40
                    run.isBold = true // Жирный
                    run.setFontColor(Color.BLACK) // Цвет
                    paragraph.setTextAlign(TextParagraph.TextAlign.CENTER)

                    // картинка
                    val pictureIdx = ppt.addPicture(
                        img,
                        getPictureTypePpt(img.absolutePath)
                    )

                    val imageFileDims = getImageDimensions(img)
                    if (imageFileDims == null) {
                        throw WrongFileException("Не картика!")
                    }

                    slide.createPicture(pictureIdx)?.let {
                        val outputWidth =
                            imageFileDims.first.toDouble() * (DEFAULT_HEIGHT_OUTPUT_PPT / imageFileDims.second)
                        val startPictureX = SLIDE_WIDTH_PPT / 2 - outputWidth.toInt() / 2
                        it.setAnchor(
                            Rectangle(
                                startPictureX,
                                100,
                                outputWidth.toInt(),
                                DEFAULT_HEIGHT_OUTPUT_PPT.toInt()
                            )
                        )
                    }

                }

                FileOutputStream(outputFile).use { out ->
                    ppt.write(out)
                }

                println("PPT создан успешно!")
                println("Generated: $outputFile")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getPictureTypeWord(imgPath: String): Int {
        val ext = imgPath.substring(imgPath.lastIndexOf(".") + 1).lowercase(Locale.getDefault())
        return when (ext) {
            "jpg", "jpeg" -> XWPFDocument.PICTURE_TYPE_JPEG
            "png" -> XWPFDocument.PICTURE_TYPE_PNG
            "gif" -> XWPFDocument.PICTURE_TYPE_GIF
            "bmp" -> XWPFDocument.PICTURE_TYPE_BMP
            else -> XWPFDocument.PICTURE_TYPE_PNG
        }
    }

    private fun getPictureTypePpt(imgPath: String): PictureData.PictureType {
        val ext = imgPath.substring(imgPath.lastIndexOf(".") + 1).lowercase(Locale.getDefault())
        return when (ext) {
            "jpg", "jpeg" -> PictureData.PictureType.JPEG
            "png" -> PictureData.PictureType.PNG
            "gif" -> PictureData.PictureType.GIF
            "bmp" -> PictureData.PictureType.BMP
            else -> PictureData.PictureType.PNG
        }
    }

    private fun getImageDimensions(file: File): Pair<Int, Int>? {
        return try {
            val image = ImageIO.read(file)
            if (image != null) {
                image.width to image.height
            } else {
                null // Не удалось прочитать изображение
            }
        } catch (e: Exception) {
            null // Ошибка чтения файла
        }
    }

    fun Pair<Int, Int>.isVertical(): Boolean = this.first < this.second

    companion object {
        private const val DEFAULT_WIDTH_OUTPUT_WORD = 350.0
        private const val DEFAULT_HEIGHT_OUTPUT_WORD = 350.0
        private const val DEFAULT_HEIGHT_OUTPUT_PPT = 400.0
        private const val SLIDE_WIDTH_PPT = 960
        private const val SLIDE_HEIGHT_PPT = 540
    }
}