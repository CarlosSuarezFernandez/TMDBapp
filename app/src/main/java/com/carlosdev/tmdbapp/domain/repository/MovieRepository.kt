package com.carlosdev.tmdbapp.domain.repository

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.TmdbMovie
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.moviebase.tmdb.model.TmdbMoviePageResult
import app.moviebase.tmdb.model.TmdbStatusResult
import com.carlosdev.tmdbapp.domain.model.OutcomeState

typealias MoviesResponse = List<TmdbMovie>
typealias MovieDetailResponse = TmdbMovieDetail
typealias FavoriteResponse = TmdbStatusResult

interface MovieRepository {
    val tmdb: Tmdb3
    suspend fun popularMovies(page: Int): MoviesResponse
    suspend fun movieDetail(id: Int): MovieDetailResponse

}