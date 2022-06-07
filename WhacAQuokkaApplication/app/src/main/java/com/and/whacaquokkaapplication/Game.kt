package com.and.whacaquokkaapplication

import android.os.Message
import android.widget.ImageView
import android.widget.TextView
import java.util.*

abstract class Game(
    protected val spawns: Array<ImageView>,
    private val scoreQuokka: TextView,
    private val scoreWhack: TextView,
    private val time: TextView
) {

    protected var lastKnownPosition = 0;

    protected fun startDisplayQuokka(pos: Int) {
        lastKnownPosition = pos
        spawns[pos - 1].setImageResource(R.drawable.hole_with_quokka)
    }

    protected fun stopDisplayQuokka(pos: Int) {
        lastKnownPosition = 0
        spawns[pos - 1].setImageResource(R.drawable.hole)
    }

    protected fun setTime(sec: Int) {

        time.text = sec.toString()
    }

    protected fun setScore(newScoreQuokka: Int, newScoreWhack: Int) {
        scoreQuokka.text = newScoreQuokka.toString()
        scoreWhack.text = newScoreWhack.toString()
    }

    abstract fun handleMessage(message: com.and.whacaquokkaapplication.models.Message)

}

class GameClient(
    spawns: Array<ImageView>,
    scoreQuokka: TextView,
    scoreWhack: TextView,
    time: TextView
) : Game(spawns, scoreQuokka, scoreWhack, time) {


    fun quokkaAppear(pos: Int) {
        if (lastKnownPosition != 0) {
            startDisplayQuokka(pos)

            // TODO send to master
        }
    }

    fun quokkaDisappear(pos: Int) {
        if (lastKnownPosition != 0) {
            stopDisplayQuokka(pos)

            // TODO sent to master
        }
    }

    override fun handleMessage(message: com.and.whacaquokkaapplication.models.Message) {

    }
}

class GameMaster(
    spawns: Array<ImageView>,
    scoreQuokka: TextView,
    scoreWhack: TextView,
    time: TextView
) : Game(spawns, scoreQuokka, scoreWhack, time) {

    private var timer: Timer? = null
    private var scoreTimer: Timer? = null

    private var seconds = 0

    private var scoreQuokaa = 0;
    private var scoreWhack = 0

    fun spawnReceived(pos: Int) {
        startDisplayQuokka(pos)

        scoreTimer = Timer()
        scoreTimer!!.schedule(object : TimerTask() {
            override fun run() {

                scoreQuokaa++
                setScore(scoreQuokaa, scoreWhack)

                // TODO update score
            }
        }, 400)
    }

    fun hitHole(pos: Int) {
        if (lastKnownPosition == pos) {
            quokkaDisappear(pos)

            scoreTimer!!.cancel()

            scoreWhack++
            setScore(scoreQuokaa, scoreWhack)

            //TODO send hit + score update
        }
    }

    fun quokkaDisappear(pos: Int) {
        stopDisplayQuokka(pos)
        scoreTimer!!.cancel()
    }

    fun startGame() {

        timer = Timer()
        seconds = 60
        timer!!.schedule(object : TimerTask() {
            override fun run() {

                seconds -= 1
                setTime(seconds)

                // TODO send time decrease to client

                if (seconds == 0) {
                    stopGame()
                }
            }
        }, 1000)
    }

    fun stopGame() {
        // TODO ?
    }

    fun exitGame() {
        timer!!.cancel()
        scoreTimer!!.cancel()
    }

    override fun handleMessage(message: com.and.whacaquokkaapplication.models.Message) {

    }

}