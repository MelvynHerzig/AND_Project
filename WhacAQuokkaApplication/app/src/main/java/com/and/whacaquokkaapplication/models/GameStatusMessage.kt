package com.and.whacaquokkaapplication.models

import com.and.whacaquokkaapplication.models.enums.GameStatus
import com.and.whacaquokkaapplication.models.enums.MessageType
import com.google.android.gms.nearby.connection.Payload
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Class that represent a game status
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class GameStatusMessage(val status: GameStatus) : Message(MessageType.GameStatus) {
    /**
     * Method that serialize the message into a payload
     */
    override fun toPayload(): Payload {
        return Payload.fromBytes(Json.encodeToString(this).toByteArray())
    }

}