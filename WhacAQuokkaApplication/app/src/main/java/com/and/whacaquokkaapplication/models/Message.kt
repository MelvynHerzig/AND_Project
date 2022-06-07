package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Class that represent a message used to communicate between phones via bluetooth
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
open class Message(val type: MessageType) {}