package com.dailystudio.tensorflow.lite.viewer.image.detector

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dailystudio.tensorflow.lite.viewer.AbsTFLiteModelViewerActivity
import com.dailystudio.tensorflow.lite.viewer.image.ImageInferenceInfo
import org.tensorflow.lite.support.label.Category
import kotlin.math.min

class MainActivity : AbsTFLiteModelViewerActivity<ImageInferenceInfo, List<Category>>() {

    companion object {
        const val REPRESENTED_ITEMS_COUNT = 3
    }

    private var detectItemViews: Array<TextView?> =
        Array(REPRESENTED_ITEMS_COUNT) {null}
    private var detectItemValueViews: Array<TextView?> =
        Array(REPRESENTED_ITEMS_COUNT) {null}

    override fun createBaseFragment(): Fragment {
        return Fragment()
    }

    override fun onResultsUpdated(results: List<Category>) {
        val itemCount = min(results.size, REPRESENTED_ITEMS_COUNT)

        for (i in 0 until itemCount) {
            detectItemViews[i]?.text = results[i].label
            detectItemValueViews[i]?.text = "%.1f%%".format(results[i].score * 100)
        }
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

    override fun createResultsView(): View? {
        return null
    }

}
