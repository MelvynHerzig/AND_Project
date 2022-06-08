package com.and.whacaquokkaapplication.gamelogic

import android.os.CountDownTimer
import android.util.Log
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.models.*

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
                if (!isGameOver() && millisUntilFinished < 120000-interval) {
                    // If game is not over increase score and send update to client.
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

    /**
     * Watch scores and indicate if the player is the winner.
     */
    override fun didIWin(): Boolean {
        return scoreQuokka.value!! < scoreWhack.value!!
    }

}