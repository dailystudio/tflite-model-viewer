package com.dailystudio.tensorflow.lite.viewer.utils

object ResultsUtils {

    fun safeToPrintableLog(`object`: Any?): String {
        return `object`?.let {
            it.toString().replace("%", "%%")
        } ?: ""
    }

}