package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Class that represent a score status from game used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class ScoreStatusMessage (
    val touched: Boolean,
    val quokkaScore: Int,
    val whackScore: Int
) : Message(MessageType.ScoreStatus) {}
