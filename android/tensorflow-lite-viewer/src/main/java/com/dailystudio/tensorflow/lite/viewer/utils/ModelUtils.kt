package com.dailystudio.tensorflow.lite.viewer.utils

import android.content.res.AssetManager
import com.dailystudio.devbricksx.development.Logger.warn
import org.tensorflow.lite.support.metadata.MetadataExtractor
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

object ModelUtils {

    @Throws(IOException::class)
    fun loadModelFile(
        assets: AssetManager,
        modelFilename: String
    ): MappedByteBuffer? {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun extractLabels(
        modelFile: MappedByteBuffer,
        labelFileName: String
    ): List<String>? {
        val metadata = MetadataExtractor(modelFile)
        val labelFile = metadata.getAssociatedFile(labelFileName) ?: return null
        val br = BufferedReader(InputStreamReader(labelFile, Charset.defaultCharset()))

        val labels = mutableListOf<String>()

        br.forEachLine {
            labels.add(it)
        }

        br.close()

        return labels
    }

}