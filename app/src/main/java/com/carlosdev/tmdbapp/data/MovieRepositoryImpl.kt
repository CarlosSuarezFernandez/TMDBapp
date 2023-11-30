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
        return try {
            val movies = tmdb.movies.popular(page, ENGLISH)
            OutcomeState.Success(movies)
        } catch (e: Exception) {
            OutcomeState.error(e)
        }
    }

    override suspend fun movieDetail(id: Int): MovieDetailResponse {
        return try {
            val movie = tmdb.movies.getDetails(id, ENGLISH, listOf(AppendResponse.MOVIE_CREDITS))
            OutcomeState.Success(movie)
        } catch (e: Exception) {
            OutcomeState.error(e)
        }
    }

    override suspend fun favoriteMovies(): MoviesResponse {
        return try {
            val movies = tmdb.account.getFavoriteMovies(ACCOUNT_ID)
            OutcomeState.Success(movies)
        } catch (e: Exception) {
            OutcomeState.error(e)
        }
    }

    override suspend fun addFavorite(id: Int): FavoriteResponse {
        return try {
            val response = tmdb.account.markFavorite(ACCOUNT_ID, TmdbMediaType.MOVIE, id, true)
            OutcomeState.Success(response)
        } catch (e: Exception) {
            OutcomeState.error(e)
        }
    }

    override suspend fun removeFavorite(id: Int): FavoriteResponse {
        return try {
            val response = tmdb.account.markFavorite(ACCOUNT_ID, TmdbMediaType.MOVIE, id, false)
            OutcomeState.Success(response)
        } catch (e: Exception) {
            OutcomeState.error(e)
        }
    }

    companion object {
        val API_KEY = "b6d7ee7674c66b07c4391f9e98c067f7"
        val ENGLISH = "en-US"
        val ACCOUNT_ID = 20766442
    }
}