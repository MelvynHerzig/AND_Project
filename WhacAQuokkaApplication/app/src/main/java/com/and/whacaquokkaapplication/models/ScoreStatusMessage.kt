package com.and.whacaquokkaapplication.models

import com.and.whacaquokkaapplication.models.enums.MessageType
import com.google.android.gms.nearby.connection.Payload
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Class that represent a score status from game used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class ScoreStatusMessage(
    val quokkaScore: Int,
    val whackScore: Int
) : Message(MessageType.ScoreStatus) {
    /**
     * Method that serialize the message into a payload
     */
    override fun toPayload(): Payload {
        return Payload.fromBytes(Json.encodeToString(this).toByteArray())
    }

}
