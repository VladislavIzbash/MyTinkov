package ru.vizbash.mytinkov.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import kotlinx.collections.immutable.toImmutableSet
import ru.vizbash.mytinkov.data.FavoritesRepository
import ru.vizbash.mytinkov.data.Phrase
import ru.vizbash.mytinkov.data.PhraseList

class MainActivity : ComponentActivity() {

    private val mediaPlayer by lazy { MediaPlayer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        var currentlyPlaying by mutableStateOf<Int?>(null)

        mediaPlayer.setOnCompletionListener {
            currentlyPlaying = null
        }

        val favoritesRepo = FavoritesRepository(this)

        setContent {
            TinkovTheme {
                Surface {
                    PhraseMenu(
                        phraseList = PhraseList,
                        favorites = favoritesRepo.favorites.collectAsState().value.toImmutableSet(),
                        onSetFavorite = { i, isFav -> favoritesRepo.setFavorite(i, isFav) },
                        onPlay = {
                            if (it == currentlyPlaying) {
                                currentlyPlaying = null
                                mediaPlayer.stop()
                            } else {
                                currentlyPlaying = it
                                playPhrase(PhraseList[it])
                            }
                        },
                        currentlyPlaying = currentlyPlaying,
                        getProgress = {
                            mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                        },
                    )
                }
            }
        }
    }

    private fun playPhrase(phrase: Phrase) {
        with(mediaPlayer) {
            if (isPlaying) {
                stop()
            }
            reset()

            resources.openRawResourceFd(phrase.resourceId).use {
                setDataSource(it)
            }
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

