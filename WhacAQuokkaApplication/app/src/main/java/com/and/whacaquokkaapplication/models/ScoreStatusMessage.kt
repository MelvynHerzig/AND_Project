package com.and.whacaquokkaapplication.models

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
    val touched: Boolean,
    val quokkaScore: Int,
    val whackScore: Int
) : Message(MessageType.ScoreStatus) {
    override fun toPayload(): Payload {
        return Payload.fromBytes(Json.encodeToString(this).toByteArray())
    }

}
