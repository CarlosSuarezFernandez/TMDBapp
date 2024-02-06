package com.carlosdev.tmdbapp.di

import com.carlosdev.tmdbapp.data.MovieRepositoryImpl
import com.carlosdev.tmdbapp.domain.repository.MovieRepository
import com.carlosdev.tmdbapp.presentation.favorites.FavoritesViewModel
import com.carlosdev.tmdbapp.presentation.home.HomeViewModel
import com.carlosdev.tmdbapp.presentation.movie.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<MovieRepository> { MovieRepositoryImpl() }
    viewModel { HomeViewModel(get()) }
    viewModel { MovieViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}