package com.carlosdev.tmdbapp.presentation.movie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.carlosdev.tmdbapp.databinding.FragmentMovieDetailBinding
import com.carlosdev.tmdbapp.domain.model.OutcomeState
import com.carlosdev.tmdbapp.presentation.home.adapter.MoviesAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }
        initPreferences()
        updateMovieId()
        loadMovie()
        bindFavoriteButton()
    }

    private fun loadMovie() {
        binding.progressComponent.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movieRequestState.collect() { outcome ->
                when (outcome) {
                    is OutcomeState.Error -> {
                        binding.progressComponent.visibility = View.GONE
                    }

                    is OutcomeState.Success -> {
                        binding.progressComponent.visibility = View.GONE
                    }

                    is OutcomeState.Progress -> {
                        binding.progressComponent.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movie.collect {
                with(binding) {
                    movieName.text = it.title
                    Picasso.get().load(BASE_URL + it.backdropPath)
                        .into(movieImage)
                    toolbarLayout.title = it.title
                    Picasso.get().load(BASE_URL + it.backdropPath)
                        .into(toolbarImage)
                    movieBudget.text = "Budget " + it.budget + "$"
                    movieRevenue.text = "Revenue " + it.revenue + "$"
                    movieRuntime.text = it.runtime.toString()
                    movieOverview.text = it.overview
                    movieReleaseDate.text = it.releaseDate.toString()
                }
            }
        }
    }

    private fun bindFavoriteButton() {

        with(binding){
            addFavoriteButton.setOnClickListener {
                viewModel.addToFavorite(arguments?.getInt("movieId").toString())
            }
            addFavoriteButton.isEnabled = !viewModel.isFavoriteMovie(arguments?.getInt("movieId").toString())
        }
    }

    private fun initPreferences(){
        viewModel.initSharedPreferences(requireContext())
    }
    private fun updateMovieId() {
        viewModel.onMovieIdReceived(arguments?.getInt("movieId") ?: 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        private const val BASE_URL = "https://image.tmdb.org/t/p/original/"
    }
}