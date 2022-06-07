package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Enum that represents the different message types that can be sent between phones.
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
enum class MessageType {
    QuokkaStatus,
    ScoreStatus,
    GameStatus
}