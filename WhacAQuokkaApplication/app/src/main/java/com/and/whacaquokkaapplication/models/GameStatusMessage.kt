package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

@Serializable
// TO use:
// https://kotlinlang.org/docs/serialization.html#example-json-serialization
// val json = Json.encodeToString(Data(42, "str"))
// val obj = Json.decodeFromString<Data>("""{"a":42, "b": "str"}""")
// val str = String(byteArray)

// test sendpayload line 173
enum class GameStatusMessage {
    START,
    STOP,
    PAUSE
}