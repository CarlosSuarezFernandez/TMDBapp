package com.carlosdev.tmdbapp.di

import com.carlosdev.tmdbapp.data.MovieRepositoryImpl
import com.carlosdev.tmdbapp.domain.repository.MovieRepository
import com.carlosdev.tmdbapp.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module{
    single<MovieRepository> {MovieRepositoryImpl()}
    viewModel {HomeViewModel(get())}
}