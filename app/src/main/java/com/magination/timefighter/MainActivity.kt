package com.magination.timefighter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    internal lateinit var tapMeButton: Button
    internal lateinit var userScore: TextView
    internal lateinit var timeLeft: TextView
    internal var score = 0

    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountdown: Long = 60000
    internal val countDownInterval: Long = 1000
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    //"var" values change while "val" values don't
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is: $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        userScore = findViewById(R.id.userScore)
        timeLeft = findViewById(R.id.timeLeft)

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if (savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }
        else{
            resetGame()
        }
    }

    //instantiates the custom menu object I created
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout){
            showInfo()
        }
        return true
    }

    @SuppressLint("StringFormatInvalid")
    private fun showInfo(){
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    //displays information to the Logcat
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeftOnTimer")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG,"onDestroy called.")
    }

    private fun resetGame(){
        score = 0
        userScore.text = getString(R.string.yourScore, score)
        val initialTimeLeft = initialCountdown/1000
        timeLeft.text = getString(R.string.timeLeft, initialTimeLeft)

        countDownTimer = object : CountDownTimer(initialCountdown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeRemaining = millisUntilFinished/1000
                timeLeft.text = getString(R.string.timeLeft, timeRemaining)
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

    private fun restoreGame(){
        userScore.text = getString(R.string.yourScore, score)

        val restoredTime = timeLeftOnTimer/1000
        timeLeft.text = getString(R.string.timeLeft, restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeRemaining = millisUntilFinished/1000
                timeLeft.text = getString(R.string.timeLeft, timeRemaining)
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true


    }

    private fun incrementScore() {
        if (!gameStarted){
            startGame()
        }
        score += 1
        val newScore = getString(R.string.yourScore, score)
        userScore.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        userScore.startAnimation(blinkAnimation)
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

}
