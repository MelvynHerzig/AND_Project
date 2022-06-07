package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Class that represent a game status
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class GameStatusMessage(val status: GameStatus) : Message(MessageType.GameStatus) {}