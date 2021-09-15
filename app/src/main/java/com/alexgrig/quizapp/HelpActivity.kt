package com.alexgrig.quizapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.TextView

private const val EXTRA_ANSWER_IS_TRUE = "com.alexgrig.quizapp.answerIsTrue"
const val EXTRA_ANSWER_SHOWN = "com.alexgrig.quizapp.answerShown"
private const val KEY_HELP = "i use help"

class HelpActivity : AppCompatActivity() {

    lateinit var showButton: Button
    lateinit var answerTextView: TextView
    private var answerIsTrue = false
    private var iUseHelp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        iUseHelp = savedInstanceState?.getBoolean(KEY_HELP) ?: false
        setAnswerShownResult(iUseHelp)

        showButton = findViewById(R.id.show_button)
        answerTextView = findViewById(R.id.answer_text)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        showButton.setOnClickListener {
            val answer = when {
                answerIsTrue -> R.string.true_button_text
                else -> R.string.false_button_text
            }
            answerTextView.setText(answer)
            answerTextView.visibility = View.VISIBLE
            iUseHelp = true
            setAnswerShownResult(iUseHelp)

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_HELP, iUseHelp)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, HelpActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}