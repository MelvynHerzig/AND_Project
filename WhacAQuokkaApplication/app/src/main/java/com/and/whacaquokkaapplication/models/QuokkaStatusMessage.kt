package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

/**
 * Data class that represent a quokka status in game used to communicate
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
@Serializable
data class QuokkaStatusMessage (
    val number: Int,
    val status: QuokkaStatus
)
