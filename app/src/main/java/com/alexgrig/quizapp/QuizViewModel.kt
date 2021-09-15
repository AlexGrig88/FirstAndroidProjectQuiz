package com.alexgrig.quizapp

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel: ViewModel() {

    var currentIndex = 0
    var correctAnswers = 0
    var alreadyAnswered = false
    var isCheater = false

    private val storageQuestions = listOf(
        Question(R.string.ant, false, R.drawable.ant),
        Question(R.string.tiger, true, R.drawable.tiger),
        Question(R.string.dolphin, true, R.drawable.dolphin),
        Question(R.string.panda, false, R.drawable.panda),
        Question(R.string.crocodile, true, R.drawable.crocodile),
        Question(R.string.mole, false, R.drawable.mole),
        Question(R.string.octopus, false, R.drawable.octopus),
        Question(R.string.pig, true, R.drawable.pig),
        Question(R.string.elephant, true, R.drawable.elephant),
        Question(R.string.dog, true, R.drawable.dog),
    )

    val currentQuestion: Int
        get() = storageQuestions[currentIndex].questionResId
    val currentAnswer: Boolean
        get() = storageQuestions[currentIndex].answer
    val currentImg: Int
        get() = storageQuestions[currentIndex].imageResId
    val amountQuestions: Int
        get() = storageQuestions.size

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    fun setupInitialStateOfGame() {
        currentIndex = 0
        correctAnswers = 0
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
}