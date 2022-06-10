package com.and.whacaquokkaapplication.models

import com.and.whacaquokkaapplication.models.enums.MessageType
import com.google.android.gms.nearby.connection.Payload
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Class that represent a message used to communicate between phones via bluetooth
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
open class Message(val type: MessageType) {
    open fun toPayload(): Payload {
        return Payload.fromBytes(Json.encodeToString(this).toByteArray())
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromPayload(payload: Payload): Message {
            val str = payload.asBytes()?.let { String(it) }
            val obj = str?.let { json.decodeFromString<Message>(it) }

            val m = when(obj!!.type){
                MessageType.GameStatus -> json.decodeFromString<GameStatusMessage>(str)
                MessageType.QuokkaStatus -> json.decodeFromString<QuokkaStatusMessage>(str)
                MessageType.ScoreStatus -> json.decodeFromString<ScoreStatusMessage>(str)
            }
            return m
        }
    }
}