package com.dailystudio.tensorflow.lite.viewer.image.detector

import android.view.View
import androidx.fragment.app.Fragment
import com.dailystudio.tensorflow.lite.viewer.AbsTFLiteModelViewerActivity
import com.dailystudio.tensorflow.lite.viewer.image.ImageInferenceInfo
import com.dailystudio.tensorflow.lite.viewer.image.detector.fragment.ObjectDetectorCameraFragment
import org.tensorflow.lite.support.label.Category

class MainActivity : AbsTFLiteModelViewerActivity<ImageInferenceInfo, List<Category>>() {

    override fun createBaseFragment(): Fragment {
        return ObjectDetectorCameraFragment()
    }

    override fun createResultsView(): View? {
        return null
    }

    override fun onResultsUpdated(results: List<Category>) {
    }

    override fun getViewerAppName(): CharSequence? {
        return getString(R.string.app_name)
    }

    override fun getAboutIconResource(): Int {
        return R.mipmap.ic_launcher
    }

    override fun getAboutThumbResource(): Int {
        return R.drawable.app_thumb
    }

}
