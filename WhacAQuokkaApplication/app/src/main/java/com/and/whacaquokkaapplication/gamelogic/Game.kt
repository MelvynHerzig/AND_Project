package com.and.whacaquokkaapplication

import android.os.CountDownTimer
import android.os.Message
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.models.*
import java.util.*

/**
 * Common game logic class shared by game client and master
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
abstract class Game {

    /**
     * Live data to spectate quokka (client) score.
     */
    val scoreQuokka: LiveData<Int> get() = _scoreQuokka

    /**
     * Live data to spectate whack (master) score.
     */
    val scoreWhack: LiveData<Int> get() = _scoreWhack

    /**
     * Live data to spectate timer.
     */
    val timer: LiveData<Int> get() = _timer

    /**
     * Live data to spectate hole to update.
     */
    val updateHoleNumber: LiveData<Int> get() = _updateHoleNumber

    /**
     * Live data to spectate when to display end screen
     */
    val gameOver: LiveData<Boolean> get() = _gameOver

    /**
     * Mutable live data of quokka (client) score.
     */
    protected val _scoreQuokka = MutableLiveData(0)

    /**
     * Mutable live data of whack (master) score.
     */
    protected val _scoreWhack = MutableLiveData(0)

    /**
     * Mutable live data of timer
     */
    protected val _timer = MutableLiveData(0)

    /**
     * Mutable live data to tell activity to update display.
     */
    protected val _updateHoleNumber = MutableLiveData(-1)

    /**
     * Mutable live data to tell activity that game is over and display end screen.
     */
    protected val _gameOver = MutableLiveData(false)

    /**
     * Timer to mesure 60s game that trigger each second.
     */
    protected var clockTimer: CountDownTimer? = null

    /**
     * Array of boolean that represent holes state. True = quokka up, False = empty.
     */
    protected val holesState = Array(9) { _ -> true }

    /**
     * Current hole with quokka up, 0 means none.
     */
    protected var currentHoleOut = 0;

    /**
     * Update time mutable live data.
     */
    protected fun setTime(sec: Int) {
        _timer.postValue(sec)
    }

    /**
     * Update live data of both scores.
     */
    protected fun setScore(newScoreQuokka: Int, newScoreWhack: Int) {
        _scoreQuokka.postValue(newScoreQuokka)
        _scoreWhack.postValue(newScoreWhack)
    }

    /**
     * Flip hole state: True <=> False, Quokka up <=> Empty.
     * and update the live data to notify UI to update.
     */
    protected fun flipHoleSate(pos: Int) {
        holesState[pos] = !holesState[pos]
        _updateHoleNumber.postValue(pos)
    }

    /**
     * Check if game is over.
     */
    protected fun isGameOver(): Boolean {
        return _gameOver.value!!
    }

    /**
     * For a given array of image view, set the corresponding image to the pos th image view.
     */
    fun updateHolesView(holes: Array<ImageView>, pos: Int) {
        if(pos == -1) return
        if (!holesState[pos]) {
            currentHoleOut = pos
            holes[pos].setImageResource(R.drawable.hole_with_quokka)
        } else {
            currentHoleOut = -1
            holes[pos].setImageResource(R.drawable.hole)
        }
    }

    /**
     * Start the game, i.e: start the 60s timer.
     */
    open fun startGame() {


        _timer.postValue(60)
        clockTimer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                _gameOver.postValue(true)
            }

            override fun onTick(millisUntilFinished: Long) {
                _timer.postValue(_timer.value!! - 1)
            }
        }.start()
    }

    /**
     * On game stop, cancel clock timer
     */
    open fun stopGame() {
        clockTimer!!.cancel()
    }

    /**
     * Handle message from bluetooth connection service.
     */
    abstract fun handleMessage(message: com.and.whacaquokkaapplication.models.Message)

}

/**
 * Game logic class made for client (spawning quokka).
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class GameClient : Game() {

    /**
     * Spawns a quokka on the pos hole and send to master the spawn event.
     */
    fun quokkaAppear(pos: Int) {
        if (!isGameOver() && currentHoleOut == -1) { // If game not over and currently no quokka out.
            flipHoleSate(pos)

            BluetoothConnectionService.send(QuokkaStatusMessage(pos.toString(), QuokkaStatus.SHOW).toPayload())
        }
    }

    /**
     * make dissapear a quokka on the pos hole and send to master the dissapear event.
     */
    fun quokkaDisappear(pos: Int) {
        if (!isGameOver() && currentHoleOut == pos) { // If game not over and the quokka is out.
            flipHoleSate(pos)

            BluetoothConnectionService.send(QuokkaStatusMessage(pos.toString(), QuokkaStatus.HIDE).toPayload())
        }
    }

    /**
     * Handle message from master.
     *
     * @param message Message to handle
     */
    override fun handleMessage(message: com.and.whacaquokkaapplication.models.Message) {
        when (message) {
            is QuokkaStatusMessage -> {
                flipHoleSate(message.pos.toInt())
            }
            is GameStatusMessage -> {
                if (message.status == GameStatus.STOP) {
                    stopGame()
                } else if (message.status == GameStatus.START) {
                    startGame()
                }
            }
            is ScoreStatusMessage -> {
                if(message.touched){
                    if(currentHoleOut != -1) {
                        flipHoleSate(currentHoleOut)
                    }
                }
                setScore(message.quokkaScore, message.whackScore)
            }
        }
    }
}

/**
 * Game logic class made for masker (whacking quokka).
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class GameMaster : Game() {

    /**
     * Timer triggered every 400ms when quokka is out to increase client score.
     */
    private var scoreTimer: CountDownTimer? = null

    /**
     * When a quakka spawning is received, start the scoring timer.
     */
    fun spawnReceived(pos: Int, startTimer : Boolean) {

        // Notify UI.
        flipHoleSate(pos)

        scoreTimer?.cancel()

        val interval :Long = 400

        // Start timer
        scoreTimer = object : CountDownTimer(120000, interval) {
            override fun onFinish() {
                scoreTimer = null
            }

            override fun onTick(millisUntilFinished: Long) {

                Log.println(Log.INFO, "Score", millisUntilFinished.toString())
                if (!isGameOver() && millisUntilFinished < 120000-interval) { // If game is not over increase score and send update to client.
                    val updatedScoreQuokka = _scoreQuokka.value!! + 1
                    val updatedScoreWhack = _scoreWhack.value!!
                    setScore(updatedScoreQuokka, updatedScoreWhack)

                    BluetoothConnectionService.send(
                        ScoreStatusMessage(
                            false,
                            updatedScoreQuokka,
                            updatedScoreWhack
                        ).toPayload()
                    )
                }
            }
        }

        if(startTimer) scoreTimer!!.start()

    }

    /**
     * When whacking a hole.
     */
    fun hitHole(pos: Int) {

        // If game is not over and quokka is up here.
        if (!isGameOver() && currentHoleOut == pos) {

            // Notify UI.
            flipHoleSate(pos)

            // Stop the scoring timer.
            scoreTimer?.cancel()

            // Update master score.
            val updatedScoreQuokka = _scoreQuokka.value!!
            val updatedScoreWhack = _scoreWhack.value!! + 1
            setScore(updatedScoreQuokka, updatedScoreWhack)

            // Notify client.
            BluetoothConnectionService.send(
                ScoreStatusMessage(
                    true,
                    updatedScoreQuokka,
                    updatedScoreWhack
                ).toPayload()
            )
        }
    }

    /**
     * On game start notify client.
     */
    override fun startGame() {
        super.startGame()

        BluetoothConnectionService.send(GameStatusMessage(GameStatus.START).toPayload())
    }

    /**
     * On game stop notify client.
     */
    override fun stopGame() {
        super.stopGame()
        scoreTimer?.cancel()

        BluetoothConnectionService.send(GameStatusMessage(GameStatus.STOP).toPayload())
    }


    /**
     * Handle message from client
     * @param message message received
     *
     */
    override fun handleMessage(message: com.and.whacaquokkaapplication.models.Message) {
        when (message) {
            is ScoreStatusMessage -> {
                throw Exception("ScoreStatusMessage should not be received by master")
            }
            is QuokkaStatusMessage -> {
                spawnReceived(message.pos.toInt(), message.status == QuokkaStatus.SHOW)
            }
            is GameStatusMessage -> {
                throw Exception("GameStatusMessage should not be received by master")
            }
        }
    }

}