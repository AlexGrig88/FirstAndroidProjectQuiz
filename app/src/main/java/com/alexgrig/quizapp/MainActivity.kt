package com.alexgrig.quizapp

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.viewModels


private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CORRECT = "correct answer"
private const val KEY_ALREADY = "already answered"
private const val KEY_FINISH = "game is finished"
private const val REQUEST_CODE_HELP = 0

class MainActivity : AppCompatActivity() {

    private val quizViewModel by viewModels<QuizViewModel>()

    lateinit var trueButton: Button
    lateinit var falseButton: Button
    lateinit var nextButton: Button
    lateinit var questionTexView: TextView
    lateinit var animalImage: ImageView
    lateinit var counterText: TextView
    lateinit var helpButton: Button

    private var isFinishedGame = false  //определяется появлением диалога, для того, чтобы не появился диалог
                                        //на последнем вопросе при повороте экрана


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(
            TAG,
            "main activity called onCreate(Bundle?). currentIndex = ${quizViewModel.currentIndex}"
        )

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)

        nextButton = findViewById(R.id.next_button)
        questionTexView = findViewById(R.id.question_text)
        animalImage = findViewById(R.id.animal_image)
        helpButton = findViewById(R.id.help_button)

        counterText = findViewById(R.id.counter_text)

        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: 0
        quizViewModel.correctAnswers = savedInstanceState?.getInt(KEY_CORRECT) ?: 0
        quizViewModel.alreadyAnswered = savedInstanceState?.getBoolean(KEY_ALREADY) ?: false
        isFinishedGame = savedInstanceState?.getBoolean(KEY_FINISH) ?: false
        setupCurrentStateOfViews()

        helpButton.setOnClickListener { view ->
            val intent = HelpActivity.newIntent(this, quizViewModel.currentAnswer)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_HELP, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_HELP)
            }
        }


        trueButton.setOnClickListener {
            switchStateButtons(false)
            quizViewModel.alreadyAnswered = true
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            switchStateButtons(false)
            quizViewModel.alreadyAnswered = true
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            onNextButtonPressed()
        }

    }


    private fun onNextButtonPressed() {
        if (!trueButton.isEnabled && !falseButton.isEnabled) {
            switchStateButtons(true)
            quizViewModel.alreadyAnswered = false
            quizViewModel.isCheater = false
            updateQuestion()
        } else {
            Toast.makeText(this, "Чтобы продолжить выберите ответ", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun setupCurrentStateOfViews() {
        if (quizViewModel.currentIndex == 9 && isFinishedGame) {
            showFinishedDialog()
        } else {
            animalImage.visibility = View.VISIBLE
            questionTexView.visibility = View.VISIBLE
            switchStateButtons(!quizViewModel.alreadyAnswered)
            nextButton.isEnabled = true
            setCurrentContent()
        }
    }

    private fun updateQuestion() {
        if (quizViewModel.currentIndex < quizViewModel.amountQuestions - 1) {
            quizViewModel.currentIndex++
            setCurrentContent()
        } else {
            showFinishedDialog()
        }

    }

    private fun setCurrentContent() {
        counterText.text = "Вопросы: ${quizViewModel.currentIndex + 1}/10"

        questionTexView.setText(quizViewModel.currentQuestion)
        animalImage.setImageResource(quizViewModel.currentImg)
    }

    private fun onToStartPressed() {
        quizViewModel.setupInitialStateOfGame()
        isFinishedGame = false
        setupCurrentStateOfViews()
    }

    private fun showFinishedDialog() {
        isFinishedGame = true
        val listener = DialogInterface.OnClickListener { _, targetButton ->
            when (targetButton) {
                DialogInterface.BUTTON_POSITIVE -> onToStartPressed()
                DialogInterface.BUTTON_NEGATIVE -> onExitPressed()
            }
        }
        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.congratulations_title_dialog)
            .setMessage("Правильных ответов: ${getPercentageAnswers().toInt()}%")
            .setPositiveButton(R.string.to_start_message, listener)
            .setNegativeButton(R.string.exit_button_dialog, listener)
            .create()
        dialog.show()
    }


    private fun checkAnswer(userAnswer: Boolean) {

        val answerMessageId = when {
            quizViewModel.isCheater -> {
                if (quizViewModel.currentAnswer == userAnswer)
                    quizViewModel.correctAnswers++
                R.string.advice_toast
            }
            quizViewModel.currentAnswer == userAnswer -> {
                quizViewModel.correctAnswers++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        val toast = Toast.makeText(this, answerMessageId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 200)
        toast.show()
    }

    private fun switchStateButtons(isEnabled: Boolean): Boolean {
        falseButton.isEnabled = isEnabled
        trueButton.isEnabled = isEnabled
        return isEnabled
    }

    private fun getPercentageAnswers() =
        (quizViewModel.correctAnswers.toDouble() / quizViewModel.amountQuestions) * 100


    private fun onExitPressed() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_HELP) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    //методы жизненного цикла Activity

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "main activity called onSaveInstanceState()")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_CORRECT, quizViewModel.correctAnswers)
        outState.putBoolean(KEY_ALREADY, quizViewModel.alreadyAnswered)
        outState.putBoolean(KEY_FINISH, isFinishedGame)

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "main activity called onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "main activity called onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "main activity called onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "main activity called onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "main activity called onDestroy()")
    }
}