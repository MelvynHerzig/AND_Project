package com.and.whacaquokkaapplication.gamelogic

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.and.whacaquokkaapplication.R
import com.and.whacaquokkaapplication.models.*
import com.and.whacaquokkaapplication.models.enums.GameStatus
import com.and.whacaquokkaapplication.models.enums.QuokkaStatus

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
     * Live data to spectate hole to update.
     */
    val updateHoleNumber: LiveData<Int> get() = _updateHoleNumber

    /**
     * Live data to spectate when to display end screen
     */
    val gameStatus: LiveData<GameStatus> get() = _gameStatus

    /**
     * Mutable live data of quokka (client) score.
     */
    protected val _scoreQuokka = MutableLiveData(0)

    /**
     * Mutable live data of whack (master) score.
     */
    protected val _scoreWhack = MutableLiveData(0)

    /**
     * Mutable live data to tell activity to update display.
     */
    protected val _updateHoleNumber = MutableLiveData(-1)

    /**
     * Mutable live data to tell activity that game is over and display end screen.
     */
    protected val _gameStatus = MutableLiveData(GameStatus.WAITING)

    /**
     * Array of boolean that represent holes state. True = quokka up, False = empty.
     */
    protected val holesState = Array(9) { _ -> true }

    /**
     * Current hole with quokka up, -1 means none.
     */
    protected var currentHoleOut = -1

    /**
     * Update live data of both scores.
     */
    protected open fun setScore(newScoreQuokka: Int, newScoreWhack: Int) {
        _scoreQuokka.postValue(newScoreQuokka)
        _scoreWhack.postValue(newScoreWhack)
    }

    /**
     * Flip hole state: True <=> False, Quokka up <=> Empty.
     * and update the live data to notify UI to update.
     */
    protected fun setHoleState(pos: Int, status : QuokkaStatus) {

        holesState[pos] = status == QuokkaStatus.HIDE
        _updateHoleNumber.postValue(pos)
    }

    /**
     * Check if game is over.
     */
    protected fun getGameStatus(): GameStatus {
        return _gameStatus.value!!
    }

    /**
     * For a given array of image view, set the corresponding image to the pos th image view.
     */
    fun updateHolesView(holes: Array<ImageView>, pos: Int) {
        if(pos == -1) return
        if (!holesState[pos] && currentHoleOut == -1) {
            currentHoleOut = pos
            holes[pos].setImageResource(R.drawable.hole_with_quokka)
        } else {
            currentHoleOut = -1
            holes[pos].setImageResource(R.drawable.hole)
        }
    }

    /**
     * Set the game status to start
     */
    open fun start(){
        _gameStatus.postValue(GameStatus.START)
    }

    /**
     * Set the game status to over
     */
    open fun stop(){
        _gameStatus.postValue(GameStatus.OVER)
    }

    /**
     * Set the visibility of the quokka at the given position
     */
    abstract fun setQuokkaVisiblity(pos: Int, status: QuokkaStatus)


    /**
     * Handle message from bluetooth connection service.
     */
    abstract fun handleMessage(message: Message)

    /**
     * Watch scores and indicate if the player is the winner.
     */
    abstract fun didIWin(): Boolean



}