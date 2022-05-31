package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

// TO use:
// https://kotlinlang.org/docs/serialization.html#example-json-serialization
// val json = Json.encodeToString(Data(42, "str"))
// val obj = Json.decodeFromString<Data>("""{"a":42, "b": "str"}""")
// val str = String(byteArray)

// test sendpayload line 173

/**
 * Enum class that represent a game status used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
enum class GameStatusMessage {
    START,
    STOP,
    PAUSE
}