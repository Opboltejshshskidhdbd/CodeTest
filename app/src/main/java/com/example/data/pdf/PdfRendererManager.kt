package com.example.data.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.data.sample.SampleStudyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PdfRendererManager(private val context: Context) {

    private var currentFileDescriptor: ParcelFileDescriptor? = null
    private var currentRenderer: PdfRenderer? = null
    private val pageBitmapCache = mutableMapOf<String, ImageBitmap>()

    fun getFileNameFromUri(uri: Uri): String {
        var name = "Document.pdf"
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            uri.lastPathSegment?.let { name = it }
        }
        return name
    }

    suspend fun openDocument(uriString: String?, sampleType: String? = null): Int = withContext(Dispatchers.IO) {
        closeCurrent()
        pageBitmapCache.clear()

        try {
            val file: File = if (uriString != null && (uriString.startsWith("content://") || uriString.startsWith("file://"))) {
                val uri = Uri.parse(uriString)
                val tempFile = File(context.cacheDir, "doc_${System.currentTimeMillis()}.pdf")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile
            } else {
                // Generate a PDF on device for the sample or use cached sample
                generateSamplePdfFile(sampleType ?: "physics")
            }

            if (file.exists() && file.length() > 0) {
                currentFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                currentFileDescriptor?.let { pfd ->
                    currentRenderer = PdfRenderer(pfd)
                    return@withContext currentRenderer?.pageCount ?: 1
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext 8
    }

    suspend fun renderPage(pageIndex: Int, targetWidth: Int = 1080): ImageBitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "$pageIndex-$targetWidth"
        pageBitmapCache[cacheKey]?.let { return@withContext it }

        val renderer = currentRenderer
        if (renderer != null && pageIndex >= 0 && pageIndex < renderer.pageCount) {
            try {
                val page = renderer.openPage(pageIndex)
                val pageWidth = page.width
                val pageHeight = page.height
                val aspectRatio = pageHeight.toFloat() / pageWidth.toFloat()
                val finalWidth = targetWidth
                val finalHeight = (targetWidth * aspectRatio).toInt().coerceIn(400, 2400)

                val bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(AndroidColor.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val imageBitmap = bitmap.asImageBitmap()
                pageBitmapCache[cacheKey] = imageBitmap
                return@withContext imageBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        null
    }

    private fun generateSamplePdfFile(sampleType: String): File {
        val file = File(context.cacheDir, "sample_$sampleType.pdf")
        if (file.exists() && file.length() > 0) return file

        val pdfDoc = PdfDocument()
        val totalPages = 8
        val pageWidth = 595 // A4 standard pt
        val pageHeight = 842

        for (i in 1..totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val content = SampleStudyData.getDocumentPageContent(sampleType, i)

            // Background
            val bgPaint = Paint().apply { color = AndroidColor.parseColor("#F8FAFC") }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            // Header Banner
            val headerPaint = Paint().apply { color = AndroidColor.parseColor("#1E1B4B") }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 70f, headerPaint)

            val textPaint = Paint().apply {
                color = AndroidColor.WHITE
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText(content.title, 28f, 42f, textPaint)

            // Subtitle
            textPaint.apply {
                color = AndroidColor.parseColor("#818CF8")
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            canvas.drawText(content.subtitle, 28f, 58f, textPaint)

            // Body text
            var curY = 105f
            content.sections.forEach { sec ->
                textPaint.apply {
                    color = AndroidColor.parseColor("#312E81")
                    textSize = 14f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText(sec.header, 32f, curY, textPaint)
                curY += 20f

                textPaint.apply {
                    color = AndroidColor.parseColor("#1E293B")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }

                val words = sec.body.split(" ")
                var line = ""
                words.forEach { w ->
                    if (textPaint.measureText("$line $w") < (pageWidth - 64)) {
                        line = if (line.isEmpty()) w else "$line $w"
                    } else {
                        canvas.drawText(line, 32f, curY, textPaint)
                        curY += 16f
                        line = w
                    }
                }
                if (line.isNotEmpty()) {
                    canvas.drawText(line, 32f, curY, textPaint)
                    curY += 24f
                }
            }

            // Key Formula Box
            val boxPaint = Paint().apply {
                color = AndroidColor.parseColor("#EEF2FF")
                style = Paint.Style.FILL
            }
            val borderPaint = Paint().apply {
                color = AndroidColor.parseColor("#6366F1")
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            val rect = RectF(30f, curY, (pageWidth - 30).toFloat(), curY + 60f)
            canvas.drawRoundRect(rect, 12f, 12f, boxPaint)
            canvas.drawRoundRect(rect, 12f, 12f, borderPaint)

            textPaint.apply {
                color = AndroidColor.parseColor("#4F46E5")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("CORE STUDY FORMULA / LAW:", 42f, curY + 22f, textPaint)

            textPaint.apply {
                color = AndroidColor.parseColor("#0F172A")
                textSize = 12f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
            canvas.drawText(content.keyFormula, 42f, curY + 44f, textPaint)

            // Footer Page Number
            textPaint.apply {
                color = AndroidColor.parseColor("#94A3B8")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            canvas.drawText("3D Study PDF System • Page $i of $totalPages", 32f, (pageHeight - 24).toFloat(), textPaint)

            pdfDoc.finishPage(page)
        }

        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
        return file
    }

    fun closeCurrent() {
        try {
            currentRenderer?.close()
            currentFileDescriptor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentRenderer = null
            currentFileDescriptor = null
        }
    }
}

