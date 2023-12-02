package com.carlosdev.tmdbapp.presentation.favorites.adapter

import android.os.Bundle
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.moviebase.tmdb.model.TmdbMovie
import app.moviebase.tmdb.model.TmdbMovieDetail
import com.carlosdev.tmdbapp.R
import com.carlosdev.tmdbapp.core.illegalState
import com.carlosdev.tmdbapp.core.layoutInflater
import com.carlosdev.tmdbapp.databinding.ItemEmptyViewBinding
import com.carlosdev.tmdbapp.databinding.MovieItemBinding
import com.squareup.picasso.Picasso

class FavoritesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var content = mutableListOf<Content>()
    private var listOfFavorites = setOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            MOVIE_ITEM_VIEW_TYPE -> MoviesViewHolder(
                MovieItemBinding.inflate(parent.layoutInflater(), parent, false)
            )

            EMPTY_ITEM_VIEW_TYPE -> EmptyViewHolder(
                ItemEmptyViewBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )

            else -> illegalState()
        }

    fun updateItems(newItems: List<TmdbMovieDetail>, favorites: Set<String>) {
        content.clear()
        if (newItems.isEmpty()) {
            content.add(Content.Empty)
        } else {
            content.addAll(newItems.map(Content::Item))
        }
        listOfFavorites = favorites
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoviesViewHolder -> {
                holder.binding.apply {
                    val currentItem = content[position] as? Content.Item ?: illegalState()
                    with(currentItem.content) {
                        Picasso.get().load(BASE_URL + posterPath).into(movieImage)
                        movieTitle.text = originalTitle
                        movieRating.text = voteAverage.toString()
                        movieYear.text = releaseDate?.year.toString()
                        listOfFavorites
                        if (listOfFavorites.contains(id.toString())) {
                            movieFavoriteMark.setImageResource(R.drawable.marked_favorite)
                        } else {
                            movieFavoriteMark.setImageResource(R.drawable.unmarked_favorite)
                        }
                    }
                }
            }

            is EmptyViewHolder -> {
                holder.binding.messageTv.text = "No movies"
            }
        }
    }

    override fun getItemCount(): Int {
        return content.size
    }

    override fun getItemViewType(position: Int): Int = when (content[position]) {
        Content.Empty -> EMPTY_ITEM_VIEW_TYPE
        is Content.Item -> MOVIE_ITEM_VIEW_TYPE
    }

    companion object {
        private const val EMPTY_ITEM_VIEW_TYPE = 100
        private const val MOVIE_ITEM_VIEW_TYPE = 102
        private const val BASE_URL = "https://image.tmdb.org/t/p/original/"
    }

    sealed class Content {
        object Empty : Content()
        data class Item(val content: TmdbMovieDetail) : Content()
    }
}

class MoviesViewHolder(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)
class EmptyViewHolder(val binding: ItemEmptyViewBinding) : RecyclerView.ViewHolder(binding.root)

