package com.carlosdev.tmdbapp.presentation.movie

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviebase.tmdb.model.TmdbMovie
import app.moviebase.tmdb.model.TmdbMovieDetail
import com.carlosdev.tmdbapp.domain.model.OutcomeState
import com.carlosdev.tmdbapp.domain.repository.MovieRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val movieIdValue = MutableStateFlow<Int>(0)
    private lateinit var sharedPreferences: SharedPreferences
    private val favoritesList: MutableList<String> = mutableListOf()

    fun onMovieIdReceived(id: Int){
        movieIdValue.value = id
    }
    private fun loadMovie() {
        viewModelScope.launch {
            val response = movieRepository.movieDetail(movieIdValue.value)
            moviesLoadState.value = response
        }
    }

    private val moviesLoadState = MutableStateFlow<TmdbMovieDetail?>(null)
    private val movieRetry = Channel<Unit>()
    val movieRequestState: Flow<OutcomeState<Unit>> =
        movieRetry.receiveAsFlow().onStart { emit(Unit) }.flatMapLatest {
            flow {
                emit(OutcomeState.progress())
                loadMovie()
                if (moviesLoadState.value == null) {
                    emit(OutcomeState.error("no movie retrieved"))
                } else {
                    emit(OutcomeState.success(Unit))
                }
            }
        }

    val movie: Flow<TmdbMovieDetail> = moviesLoadState.filterNotNull()

    fun addToFavorite(id: String){
        if(!isFavoriteMovie(id)){
            favoritesList.add(id)
            updateSharedPreferences()
        }
    }

    fun isFavoriteMovie(movieId: String): Boolean {
        return favoritesList.contains(movieId)
    }

    fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)
        // Cargar la lista de IDs de películas favoritas al iniciar el ViewModel
        favoritesList.addAll(sharedPreferences.getStringSet("favorites", setOf()) ?: setOf())
    }

    private fun updateSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("favorites", favoritesList.toSet())
        editor.apply()
    }

}