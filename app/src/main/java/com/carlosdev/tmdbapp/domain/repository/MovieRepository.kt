package com.carlosdev.tmdbapp.domain.repository

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.moviebase.tmdb.model.TmdbMoviePageResult
import app.moviebase.tmdb.model.TmdbStatusResult

interface MovieRepository {
    val tmdb: Tmdb3
    suspend fun popularMovies(page: Int): TmdbMoviePageResult
    suspend fun movieDetail(id: Int): TmdbMovieDetail
    suspend fun favoriteMovies(): TmdbMoviePageResult
    suspend fun addFavorite(id: Int): TmdbStatusResult
    suspend fun removeFavorite(id: Int): TmdbStatusResult
}