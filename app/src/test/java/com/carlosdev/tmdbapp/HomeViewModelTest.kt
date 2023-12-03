package com.carlosdev.tmdbapp

import app.moviebase.tmdb.model.TmdbMovie
import com.carlosdev.tmdbapp.domain.model.OutcomeState
import com.carlosdev.tmdbapp.domain.repository.MovieRepository
import com.carlosdev.tmdbapp.presentation.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: MovieRepository
    private val testDispatcher = TestCoroutineDispatcher()

    private val movie1 = TmdbMovie(
        backdropPath = "path1",
        posterPath = "poster1",
        adult = false,
        overview = "overview1",
        releaseDate = LocalDate(2023, 12, 1),
        genresIds = listOf(1,2),
        id = 1,
        originalTitle = "title1",
        originalLanguage = "language1",
        title = "title1",
        popularity = 0F,
        voteCount = 1,
        video = false,
        voteAverage = 0F,
    )

    @Before
    fun setup() {
        repository = mockk<MovieRepository>()
        viewModel = HomeViewModel(repository)
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun givenValidListWhenRequestThenSuccess() = runTest {

        val moviesList = listOf(movie1)

        coEvery { repository.popularMovies(1) } returns moviesList

        viewModel.movieRequestState.test {
            assertTrue(next() is OutcomeState.Progress)
            val next = next()
            assertTrue(next is OutcomeState.Success)
        }
    }

    @Test
    fun givenEmptyListWhenRequestThenError() = runTest {

        coEvery { repository.popularMovies(1) } returns emptyList()

        viewModel.movieRequestState.test {
            assertTrue(next() is OutcomeState.Progress)
            val next = next()
            assertTrue(next is OutcomeState.Error)
        }
    }

}
