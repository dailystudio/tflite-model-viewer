package com.dailystudio.tensorflow.lite.viewer

import com.nostra13.universalimageloader.core.DisplayImageOptions

object Constants {

    const val ACTION_MAIN = "com.dailystudio.tflite.example.ACTION_MAIN"
    const val EXAMPLE_ACTIVITY_CLASS_NAME = ".ExampleActivity"

    val DEFAULT_IMAGE_LOADER_OPTIONS_BUILDER: DisplayImageOptions.Builder = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(false)
        .showImageOnLoading(R.color.transparent)
        .showImageOnFail(R.color.transparent)
        .showImageForEmptyUri(R.color.transparent)
        .resetViewBeforeLoading(true)

    const val EVENT_INFERENCE_INFO_UPDATE = "inference-info-update"
    const val EVENT_RESULTS_UPDATE = "results-update"
    const val EVENT_SETTINGS_CHANGE = "settings-change"
}