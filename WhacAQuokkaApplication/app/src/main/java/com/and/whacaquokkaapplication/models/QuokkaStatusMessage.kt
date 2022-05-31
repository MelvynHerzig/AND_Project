package com.and.whacaquokkaapplication.models

import kotlinx.serialization.Serializable

@Serializable
data class QuokkaStatusMessage (
    val number: Int,
    val status: QuokkaStatus
)
