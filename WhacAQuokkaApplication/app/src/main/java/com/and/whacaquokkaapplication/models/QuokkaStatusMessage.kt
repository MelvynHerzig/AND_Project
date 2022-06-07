package com.and.whacaquokkaapplication.models

import com.google.android.gms.nearby.connection.Payload
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Class that represent a quokka status in game used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class QuokkaStatusMessage (
    val pos: String,
    val status: QuokkaStatus
) : Message(MessageType.QuokkaStatus) {
    override fun toPayload(): Payload {
        return Payload.fromBytes(Json.encodeToString(this).toByteArray())
    }

}
