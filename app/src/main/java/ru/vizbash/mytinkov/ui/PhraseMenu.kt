package ru.vizbash.mytinkov.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import ru.vizbash.mytinkov.data.Phrase
import ru.vizbash.mytinkov.data.PhraseList

private const val progressUpdateDelayMs = 50L

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhraseMenu(
    phraseList: ImmutableList<Phrase>,
    favorites: ImmutableSet<Int>,
    onPlay: (Int) -> Unit,
    onSetFavorite: (Int, Boolean) -> Unit,
    currentlyPlaying: Int?,
    getProgress: () -> Float,
) {
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    val sortedPhrases = phraseList
        .withIndex()
        .sortedByDescending { favorites.contains(it.index) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues()
    ) {
        items(
            items = sortedPhrases,
            key = { it.index },
        ) { (index, phrase) ->
            val isPlaying = index == currentlyPlaying

            PhraseItem(
                modifier = Modifier.animateItemPlacement(),
                phrase = phrase,
                isFavorite = favorites.contains(index),
                onClick = { onPlay(index) },
                onSetFavorite = {
                    onSetFavorite(index, it)
                },
                isPlaying = isPlaying,
                getProgress = { playbackProgress },
            )
            Divider(Modifier.fillMaxWidth().padding(horizontal = 8.dp))
        }
    }

    LaunchedEffect(currentlyPlaying) {
        while (currentlyPlaying != null) {
            playbackProgress = getProgress()
            delay(progressUpdateDelayMs)
        }
    }
}

@Composable
private fun PhraseItem(
    modifier: Modifier = Modifier,
    phrase: Phrase,
    isFavorite: Boolean,
    onSetFavorite: (Boolean) -> Unit,
    onClick: () -> Unit,
    isPlaying: Boolean,
    getProgress: () -> Float,
) {
    val progressBg = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() }
            .drawBehind {
                if (isPlaying) {
                    drawRect(
                        color = progressBg,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width * getProgress(), size.height),
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.padding(start = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                if (isPlaying) {
                    Icon(Icons.Default.Stop, null)
                } else {
                    Icon(Icons.Default.PlayArrow, null)
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 2,
            text = phrase.text,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = phrase.duration.toComponents { mins, secs, _ ->
                "$mins:${secs.toString().padStart(2, '0')}"
            },
            color = MaterialTheme.colorScheme.outline
        )
        IconButton(
            onClick = {
                onSetFavorite(!isFavorite)
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            )
        ) {
            if (isFavorite) {
                Icon(Icons.Default.Star, null)
            } else {
                Icon(Icons.Default.StarBorder, null)
            }
        }
    }
}

@Preview
@Composable
private fun PhraseMenuPreview() {
    TinkovTheme {
        Surface {
            PhraseMenu(
                phraseList = PhraseList,
                onPlay = {},
                favorites = persistentSetOf(2, 4),
                onSetFavorite = { _, _ -> },
                currentlyPlaying = 1,
                getProgress = { 0.4f }
            )
        }
    }
}

