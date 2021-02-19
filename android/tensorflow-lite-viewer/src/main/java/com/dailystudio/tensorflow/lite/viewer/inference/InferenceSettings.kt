package com.dailystudio.tensorflow.lite.viewer.inference

import org.tensorflow.lite.support.model.Model.Device

class InferenceSettings(val device: Device,
                        val numOfThreads: Int = 1) {

    override fun toString(): String {
        return buildString {
            append("device: $device, ")
            append("numOfThreads: $numOfThreads")
        }
    }

}