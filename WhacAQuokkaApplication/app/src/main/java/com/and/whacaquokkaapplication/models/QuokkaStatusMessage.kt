package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Class that represent a quokka status in game used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
class QuokkaStatusMessage (
    val number: Int,
    val status: QuokkaStatus
) : Message(MessageType.QuokkaStatus) {}
