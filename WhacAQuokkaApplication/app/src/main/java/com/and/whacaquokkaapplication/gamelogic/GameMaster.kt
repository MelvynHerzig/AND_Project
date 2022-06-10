package com.and.whacaquokkaapplication.gamelogic

import android.os.CountDownTimer
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.models.*
import com.and.whacaquokkaapplication.models.enums.GameStatus
import com.and.whacaquokkaapplication.models.enums.QuokkaStatus

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
     * Show or hide quokka at given position
     * When showing a quokka, start timer to increase score.
     * when hidding a quokka, stop timer to increase score.
     *
     * @param pos Position of quokka to show or hide
     * @param status Enum to show or hide quokka
     */
    override fun setQuokkaVisiblity(pos: Int, status: QuokkaStatus) {
        if (isGameOver()) return
        if (status == QuokkaStatus.HIDE && pos == currentHoleOut) {
            scoreTimer?.cancel()
            setHoleState(pos, status)
        }
        if (status == QuokkaStatus.SHOW && pos != -1) {
            scoreTimer?.cancel()
            setHoleState(pos, status)

            val interval: Long = 400

            // Start timer
            scoreTimer = object : CountDownTimer(120000, interval) {
                override fun onFinish() {
                    scoreTimer = null
                }

                override fun onTick(millisUntilFinished: Long) {

                    if (!isGameOver() && millisUntilFinished < 120000 - interval) {
                        // If game is not over increase score and send update to client.
                        val updatedScoreQuokka = _scoreQuokka.value!! + 1
                        val updatedScoreWhack = _scoreWhack.value!!
                        setScore(updatedScoreQuokka, updatedScoreWhack)

                        BluetoothConnectionService.send(
                            ScoreStatusMessage(
                                updatedScoreQuokka,
                                updatedScoreWhack
                            ).toPayload()
                        )
                    }
                }
            }
            scoreTimer?.start()
        }
    }

    /**
     * When whacking a hole.
     */
    fun hitHole(pos: Int) {

        // If game is not over and quokka is up here.
        if (!isGameOver() && currentHoleOut == pos) {

            // Notify UI.
            setQuokkaVisiblity(pos, QuokkaStatus.HIDE)

            // Stop the scoring timer.
            scoreTimer?.cancel()

            // Update master score.
            val updatedScoreQuokka = _scoreQuokka.value!!
            val updatedScoreWhack = _scoreWhack.value!! + 1
            setScore(updatedScoreQuokka, updatedScoreWhack)

            // Notify client.
            BluetoothConnectionService.send(
                QuokkaStatusMessage(
                    pos,
                    QuokkaStatus.HIDE
                ).toPayload()
            )
            BluetoothConnectionService.send(
                ScoreStatusMessage(
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
    override fun handleMessage(message: Message) {
        when (message) {
            is ScoreStatusMessage -> {
                throw Exception("ScoreStatusMessage should not be received by master")
            }
            is QuokkaStatusMessage -> {
                setQuokkaVisiblity(message.pos, message.status)
            }
            is GameStatusMessage -> {
                throw Exception("GameStatusMessage should not be received by master")
            }
        }
    }

    /**
     * Watch scores and indicate if the player is the winner.
     */
    override fun didIWin(): Boolean {
        return scoreQuokka.value!! < scoreWhack.value!!
    }

}