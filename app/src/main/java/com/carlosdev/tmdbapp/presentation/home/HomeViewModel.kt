package com.carlosdev.tmdbapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviebase.tmdb.model.TmdbMovie
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

class HomeViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private fun loadMovies() {
        viewModelScope.launch {
            val response = movieRepository.popularMovies(1)
            moviesLoadState.value = response
        }
    }

    private val moviesLoadState = MutableStateFlow<List<TmdbMovie>?>(null)
    private val movieRetry = Channel<Unit>()
    val movieRequestState: Flow<OutcomeState<Unit>> =
        movieRetry.receiveAsFlow().onStart { emit(Unit) }.flatMapLatest {
            flow {
                emit(OutcomeState.progress())
                loadMovies()
                if (moviesLoadState.value.isNullOrEmpty()) {
                    emit(OutcomeState.error("no movies retrieved"))
                } else {
                    emit(OutcomeState.success(Unit))
                }
            }
        }

    val movies: Flow<List<TmdbMovie>> = moviesLoadState.filterNotNull()

}