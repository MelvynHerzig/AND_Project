package com.and.whacaquokkaapplication.models

import com.google.android.gms.nearby.connection.Payload
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// To use:
// https://kotlinlang.org/docs/serialization.html#example-json-serialization
// val json = Json.encodeToString(Data(42, "str"))
// val obj = Json.decodeFromString<Data>("""{"a":42, "b": "str"}""")
// val str = String(byteArray)

// test sendpayload line 173

/**
 * Class that represent a game status
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class GameStatusMessage(val status: GameStatus) : Message(MessageType.GameStatus) {
    override fun toPayload(): Payload {
        return Payload.fromBytes(Json.encodeToString(this).toByteArray())
    }

}