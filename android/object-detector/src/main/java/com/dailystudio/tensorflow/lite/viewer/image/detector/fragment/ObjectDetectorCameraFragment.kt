package com.dailystudio.tensorflow.lite.viewer.image.detector.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.MatrixUtils
import com.dailystudio.tensorflow.lite.viewer.image.AbsTFLiteCameraFragment
import com.dailystudio.tensorflow.lite.viewer.image.AbsTFLiteImageAnalyzer
import com.dailystudio.tensorflow.lite.viewer.image.ImageInferenceInfo
import com.dailystudio.tensorflow.lite.viewer.image.detector.ml.LiteModel
import com.dailystudio.tensorflow.lite.viewer.utils.ModelUtils
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.metadata.MetadataExtractor
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.charset.Charset
import kotlin.math.min

class Detection(
    val id: String,
    val title: String,
    val confidence: Float,
    val location: RectF
) {
    override fun toString(): String {
        return buildString {
            append("[$id]: ")
            append("title = $title,")
            append("confidence = $confidence,")
            append("location = $location")
        }
    }
}

class ObjectDetectorAnalyzer(rotation: Int, lensFacing: Int)
    : AbsTFLiteImageAnalyzer<ImageInferenceInfo, List<Category>>(rotation, lensFacing) {

    companion object {
        private const val NUM_DETECTIONS = 10

        private const val TF_LITE_MODEL_FILE = "lite-model.tflite"
        private const val TF_OD_API_LABELS_FILE = "labelmap.txt"

        private val LABELS = mutableListOf<String>()
    }

    private val detector: LiteModel? by lazy {
        GlobalContextWrapper.context?.let {

            val modelFile: MappedByteBuffer? =
                ModelUtils.loadModelFile(
                    it.assets,
                    TF_LITE_MODEL_FILE
                )

            modelFile?.let { model ->
                val labels = ModelUtils.extractLabels(model, TF_OD_API_LABELS_FILE)

                labels?.let { labels ->
                    LABELS.addAll(labels)
                }
            }

            LiteModel.newInstance(it)
        }
    }

    override fun analyzeFrame(context: Context,
                              inferenceBitmap: Bitmap,
                              info: ImageInferenceInfo): List<Category>? {
        val tImage = TensorImage.fromBitmap(inferenceBitmap)
        val inputSize = tImage.width

        val outputs = detector?.process(tImage.tensorBuffer)

        outputs?.let {
            val outputLocations = it.locationAsTensorBuffer
            val outputCategories = it.categoryAsTensorBuffer
            val outputScores = it.scoreAsTensorBuffer
            val outputNumberOfDetections = it.numberOfDetectionsAsTensorBuffer

            val numDetectionsOutput = min(
                NUM_DETECTIONS,
                outputNumberOfDetections.getFloatValue(0).toInt()
            )

            val detections = mutableListOf<Detection>()
            for (i in 0 until numDetectionsOutput) {
                val location = RectF(
                    outputLocations.floatArray[i * 4 + 1] * inputSize,
                    outputLocations.floatArray[i * 4 + 0] * inputSize,
                    outputLocations.floatArray[i * 4 + 3] * inputSize,
                    outputLocations.floatArray[i * 4 + 2] * inputSize
                )

                detections.add(
                    Detection(
                        "" + i,
                        LABELS[outputCategories.getFloatValue(i).toInt()],
                        outputScores.getFloatValue(i),
                        location
                    )
                )
            }
            Logger.debug("detections = $detections")
        }

        return null
    }

    override fun createInferenceInfo(): ImageInferenceInfo {
        return ImageInferenceInfo()
    }

    override fun preProcessImage(
        frameBitmap: Bitmap?,
        info: ImageInferenceInfo
    ): Bitmap? {
        if (frameBitmap == null) {
            return frameBitmap
        }

        val matrix = MatrixUtils.getTransformationMatrix(
            frameBitmap.width,
            frameBitmap.height, 300, 300, info.imageRotation,
            true, true
        )

        return ImageUtils.createTransformedBitmap(
            frameBitmap,
            matrix
        )
    }

}

class ObjectDetectorCameraFragment
    : AbsTFLiteCameraFragment<ImageInferenceInfo, List<Category>>() {

    override fun createAnalyzer(screenAspectRatio: Int, rotation: Int, lensFacing: Int): AbsTFLiteImageAnalyzer<ImageInferenceInfo, List<Category>> {
        return ObjectDetectorAnalyzer(rotation, lensFacing)
    }

}