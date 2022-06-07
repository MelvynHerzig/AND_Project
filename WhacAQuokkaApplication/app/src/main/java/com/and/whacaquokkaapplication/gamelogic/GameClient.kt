package com.and.whacaquokkaapplication.gamelogic

import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.models.*

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

    /**
     * Watch scores and indicate if the player is the winner.
     */
    override fun didIWin(): Boolean {
        return scoreQuokka.value!! > scoreWhack.value!!
    }
}