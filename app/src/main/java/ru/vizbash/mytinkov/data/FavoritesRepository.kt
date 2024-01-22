package ru.vizbash.mytinkov.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val sharedPrefName = "favorites"
private const val sharedPrefKey = "favorites"

class FavoritesRepository(context: Context) {
    private val sharedPrefs = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

    private val _favorites: MutableStateFlow<Set<Int>>
    val favorites get() = _favorites.asStateFlow()

    init {
        val favs = sharedPrefs.getStringSet(sharedPrefKey, null) ?: setOf()
        _favorites = MutableStateFlow(favs.map(String::toInt).toSet())
    }

    fun setFavorite(phraseIndex: Int, favorite: Boolean) {
        if (favorite) {
            _favorites.value += phraseIndex
        } else {
            _favorites.value -= phraseIndex
        }

        sharedPrefs.edit()
            .putStringSet(sharedPrefKey, _favorites.value.map(Int::toString).toSet())
            .apply()
    }
}