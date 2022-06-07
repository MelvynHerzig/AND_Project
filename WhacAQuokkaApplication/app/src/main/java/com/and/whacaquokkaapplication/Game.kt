package com.and.whacaquokkaapplication

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * Common game logic class shared by game client and master
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
abstract class Game () {

    /**
     * Live data to spectate quokka (client) score.
     */
    val scoreQuokka : LiveData<Int> get() = _scoreQuokka

    /**
     * Live data to spectate whack (master) score.
     */
    val scoreWhack : LiveData<Int> get() = _scoreWhack

    /**
     * Live data to spectate timer.
     */
    val timer : LiveData<Int> get() = _timer

    /**
     * Live data to spectate hole to update.
     */
    val updateHoleNumber : LiveData<Int> get() = _updateHoleNumber

    /**
     * Live data to spectate when to display end screen
     */
    val gameOver : LiveData<Boolean> get() = _gameOver

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
    protected val _updateHoleNumber = MutableLiveData(0)

    /**
     * Mutable live data to tell activity that game is over and display end screen.
     */
    protected val _gameOver = MutableLiveData(false)

    /**
     * Timer to mesure 60s game that trigger each second.
     */
    protected var clockTimer : Timer? = null

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
    protected fun isGameOver() : Boolean {
        return _gameOver.value!!
    }

    /**
     * For a given array of image view, set the corresponding image to the pos th image view.
     */
    fun updateHolesView(holes: Array<ImageView>, pos: Int) {
        if(!holesState[pos]) {
            currentHoleOut = pos
            holes[pos - 1].setImageResource(R.drawable.hole_with_quokka)
        } else {
            currentHoleOut = 0
            holes[pos - 1].setImageResource(R.drawable.hole)
        }
    }

    /**
     * Start the game, i.e: start the 60s timer.
     */
    open fun startGame() {

        clockTimer = Timer()
        _timer.postValue(60)
        clockTimer!!.schedule(object : TimerTask() {
            override fun run() {

                val updatedTimer = _timer.value!! + 1
                _timer.postValue(updatedTimer)
                if (updatedTimer == 0) {
                    _gameOver.postValue(true)
                }
            }
        }, 1000) // Each second tick to decrease timer mutable live data
    }

    /**
     * On game stop, cancel clock timer
     */
    open fun stopGame() {
        clockTimer!!.cancel()
    }

    // TODO comment
    abstract fun handleMessage()

}

/**
 * Game logic class made for client (spawning quokka).
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class GameClient () : Game() {

    /**
     * Spawns a quokka on the pos hole and send to master the spawn event.
     */
    fun quokkaAppear(pos: Int) {
        if (!isGameOver() && currentHoleOut == 0){ // If game not over and currently no quokka out.
            flipHoleSate(pos)

            // TODO send to master that quokka spawned
        }
    }

    /**
     * make dissapear a quokka on the pos hole and send to master the dissapear event.
     */
    fun quokkaDisappear(pos: Int) {
        if (!isGameOver() && currentHoleOut == pos) { // If game not over and the quokka is out.
            flipHoleSate(pos)

            // TODO sent to master that quokka despawned
        }
    }

    // TODO comment
    override fun handleMessage(){
        // TODO handle commands
    }
}

/**
 * Game logic class made for masker (whacking quokka).
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class GameMaster () : Game() {

    /**
     * Timer triggered every 400ms when quokka is out to increase client score.
     */
    private var scoreTimer : Timer? = null

    /**
     * When a quakka spawning is received, start the scoring timer.
     */
    fun spawnReceived(pos : Int) {

        // Notify UI.
        flipHoleSate(pos)

        // Start timer
        scoreTimer = Timer()
        scoreTimer!!.schedule(object : TimerTask() {
            override fun run() {


                // If game is not over increase score and send update to client.
                if(!isGameOver()){
                    val updatedScoreQuokka = _scoreQuokka.value!! + 1
                    val updatedScoreWhack = _scoreWhack.value!!
                    setScore(updatedScoreQuokka, updatedScoreWhack)

                    // TODO send to client score updates
                }
            }
        }, 400)
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
            scoreTimer!!.cancel()

            // Update master score.
            val updatedScoreQuokka = _scoreQuokka.value!!
            val updatedScoreWhack = _scoreWhack.value!! + 1
            setScore(updatedScoreQuokka, updatedScoreWhack)

            // Notify client.
            //TODO send quokka update + score updates
        }
    }

    /**
     * On game start notify client.
     */
    override fun startGame() {
        super.startGame()

        // TODO send to client game start
    }

    /**
     * On game stop notify client.
     */
    override fun stopGame() {
        super.stopGame()
        scoreTimer!!.cancel()

        // TODO send to client game stop
    }


    // TODO commment
    override fun handleMessage(){
        // TODO handling messages
    }
}