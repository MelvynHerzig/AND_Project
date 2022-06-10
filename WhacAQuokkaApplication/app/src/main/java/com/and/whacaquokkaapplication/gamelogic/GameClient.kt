package com.and.whacaquokkaapplication.gamelogic

import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.models.*
import com.and.whacaquokkaapplication.models.enums.GameStatus
import com.and.whacaquokkaapplication.models.enums.QuokkaStatus

/**
 * Game logic class made for client (spawning quokka).
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class GameClient : Game() {

    /**
     * If possible, set the quokka visible or hidden at the given position.
     * Send the command to the server.
     */
    override fun setQuokkaVisiblity(pos: Int, status: QuokkaStatus) {
        if (isGameOver()) return
        if (pos == -1 && status == QuokkaStatus.SHOW) return
        if (status == QuokkaStatus.HIDE && pos != currentHoleOut) return
        setHoleState(pos, status)
        BluetoothConnectionService.send(
            QuokkaStatusMessage(
                pos,
                status
            ).toPayload()
        )
    }

    /**
     * Handle message from master.
     *
     * @param message Message to handle
     */
    override fun handleMessage(message: Message) {
        when (message) {
            is QuokkaStatusMessage -> {
                setQuokkaVisiblity(message.pos, message.status)
            }
            is GameStatusMessage -> {
                if (message.status == GameStatus.STOP) {
                    stopGame()
                } else if (message.status == GameStatus.START) {
                    startGame()
                }
            }
            is ScoreStatusMessage -> {
                setScore(message.quokkaScore, message.whackScore)
            }
        }
    }

    /**
     * Watch scores and indicate if the player is the winner.
     */
    override fun didIWin(): Boolean {
        return scoreQuokka.value!! > scoreWhack.value!!
    }
}