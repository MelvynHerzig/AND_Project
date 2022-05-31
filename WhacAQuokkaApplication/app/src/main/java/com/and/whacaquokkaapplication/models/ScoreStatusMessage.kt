package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

@Serializable
data class ScoreStatusMessage (
    val touched: Boolean,
    val quokkaScore: Int,
    val whackScore: Int
)
