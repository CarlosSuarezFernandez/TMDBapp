package com.carlosdev.tmdbapp.domain.repository

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.moviebase.tmdb.model.TmdbMoviePageResult
import app.moviebase.tmdb.model.TmdbStatusResult
import com.carlosdev.tmdbapp.domain.model.OutcomeState

typealias MoviesResponse = OutcomeState<TmdbMoviePageResult>
typealias MovieDetailResponse = OutcomeState<TmdbMovieDetail>
typealias FavoriteResponse = OutcomeState<TmdbStatusResult>

interface MovieRepository {
    val tmdb: Tmdb3
    suspend fun popularMovies(page: Int): MoviesResponse
    suspend fun movieDetail(id: Int): MovieDetailResponse
    suspend fun favoriteMovies(): MoviesResponse
    suspend fun addFavorite(id: Int): FavoriteResponse
    suspend fun removeFavorite(id: Int): FavoriteResponse
}