package com.dailystudio.tensorflow.lite.viewer.image.classifier

import com.dailystudio.tensorflow.lite.viewer.BaseTFLiteModelViewApplication

class ViewerApplication : BaseTFLiteModelViewApplication() {

    override fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

}
