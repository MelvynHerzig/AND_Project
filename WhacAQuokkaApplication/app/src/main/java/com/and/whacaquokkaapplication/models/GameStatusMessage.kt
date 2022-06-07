package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

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
class GameStatusMessage(val status: GameStatus) : Message(MessageType.GameStatus) {}