package com.team5417.frcscouting

import androidx.annotation.ColorInt

sealed class DataModel {
    data class Header(
        val title: String
    ) : DataModel()
    data class Number(
        val title: String,
        var value: Int,
        var step: Int = 1,
        var min: Int = 0,
        var max: Int = 999
    ) : DataModel()
    data class Slider(
        val title: String,
        var value: Float,
        var min: Float = 0.0f,
        var max: Float = 999.0f,
        var step: Float = 1.0f
    ) : DataModel()
    data class Checkbox(
        val title: String,
        var value: Boolean
    ) : DataModel()
    data class Text(
        val title: String,
        var value: String
    ) : DataModel()
}