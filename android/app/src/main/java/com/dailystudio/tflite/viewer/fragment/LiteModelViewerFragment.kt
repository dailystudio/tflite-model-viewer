package com.dailystudio.tflite.viewer.fragment

import android.graphics.Bitmap
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.utils.ImageUtils
import com.dailystudio.devbricksx.utils.MatrixUtils
import com.dailystudio.tflite.example.common.image.AbsExampleCameraFragment
import com.dailystudio.tflite.example.common.image.AbsImageAnalyzer
import com.dailystudio.tflite.example.common.image.ImageInferenceInfo
import com.dailystudio.tflite.viewer.ml.LiteModelAiyVisionClassifierBirdsV13
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category


class LiteModelImageAnalyzer(rotation: Int, lensFacing: Int)
    : AbsImageAnalyzer<ImageInferenceInfo, List<Category>>(rotation, lensFacing) {

    private var classifier: LiteModelAiyVisionClassifierBirdsV13? = null

    override fun analyzeFrame(
        inferenceBitmap: Bitmap,
        info: ImageInferenceInfo
    ): List<Category>? {
        if (classifier == null) {
            val context = GlobalContextWrapper.context
            context?.let {
                classifier = LiteModelAiyVisionClassifierBirdsV13.newInstance(context)
            }
        }

        val tImage = TensorImage.fromBitmap(inferenceBitmap)

        val categories = classifier?.process(tImage)?.probabilityAsCategoryList

        categories?.sortByDescending {
            it.score
        }

        return categories
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
            frameBitmap.height, 640, 480, info.imageRotation, true
        )

        return ImageUtils.createTransformedBitmap(
            frameBitmap,
            matrix
        )
    }

}


class LiteModelViewerFragment
    : AbsExampleCameraFragment<ImageInferenceInfo, List<Category>>() {

    override fun createAnalyzer(screenAspectRatio: Int, rotation: Int, lensFacing: Int): AbsImageAnalyzer<ImageInferenceInfo, List<Category>> {
        return LiteModelImageAnalyzer(rotation, lensFacing)
    }

}