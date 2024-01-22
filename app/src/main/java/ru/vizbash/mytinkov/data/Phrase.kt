package ru.vizbash.mytinkov.data

import androidx.annotation.RawRes
import kotlin.time.Duration

data class Phrase(
    val text: String,
    val duration: Duration,
    @RawRes val resourceId: Int,
)
