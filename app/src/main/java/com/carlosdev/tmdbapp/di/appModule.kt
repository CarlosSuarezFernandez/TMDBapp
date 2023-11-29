package com.carlosdev.tmdbapp.di

import com.carlosdev.tmdbapp.data.MovieRepositoryImpl
import com.carlosdev.tmdbapp.domain.repository.MovieRepository
import org.koin.dsl.module

val appModule = module{
    single<MovieRepository> {MovieRepositoryImpl()}
}