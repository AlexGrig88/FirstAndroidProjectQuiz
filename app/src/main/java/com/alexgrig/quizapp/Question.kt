package com.alexgrig.quizapp

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Question(
    @StringRes val questionResId: Int,
    val answer: Boolean,
    @DrawableRes val imageResId: Int)