package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Enum class that represent a game status used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
enum class GameStatus {
    START,
    STOP,
    PAUSE
}