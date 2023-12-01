package com.carlosdev.tmdbapp.data

import app.moviebase.tmdb.Tmdb3
import app.moviebase.tmdb.model.AppendResponse
import app.moviebase.tmdb.model.TmdbMediaType
import app.moviebase.tmdb.model.TmdbMovieDetail
import app.moviebase.tmdb.model.TmdbMoviePageResult
import app.moviebase.tmdb.model.TmdbStatusResult
import com.carlosdev.tmdbapp.domain.model.OutcomeState
import com.carlosdev.tmdbapp.domain.repository.FavoriteResponse
import com.carlosdev.tmdbapp.domain.repository.MovieDetailResponse
import com.carlosdev.tmdbapp.domain.repository.MovieRepository
import com.carlosdev.tmdbapp.domain.repository.MoviesResponse

class MovieRepositoryImpl() : MovieRepository {

    override val tmdb = Tmdb3(API_KEY)

    override suspend fun popularMovies(page: Int): MoviesResponse {
        val response = tmdb.movies.popular(page, ENGLISH).results
        return response
    }

    override suspend fun movieDetail(id: Int): MovieDetailResponse {
        return tmdb.movies.getDetails(id, ENGLISH, listOf(AppendResponse.MOVIE_CREDITS))
    }

    override suspend fun favoriteMovies(): MoviesResponse {
        return tmdb.account.getFavoriteMovies(ACCOUNT_ID).results
    }

    override suspend fun addFavorite(id: Int): FavoriteResponse {
        return tmdb.account.markFavorite(ACCOUNT_ID, TmdbMediaType.MOVIE, id, true)
    }

    override suspend fun removeFavorite(id: Int): FavoriteResponse {
        return tmdb.account.markFavorite(ACCOUNT_ID, TmdbMediaType.MOVIE, id, false)
    }

    companion object {
        val API_KEY = "b6d7ee7674c66b07c4391f9e98c067f7"
        val ENGLISH = "en-US"
        val ACCOUNT_ID = 20766442
    }
}