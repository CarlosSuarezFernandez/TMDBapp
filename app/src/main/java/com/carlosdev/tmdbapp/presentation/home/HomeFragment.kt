package com.carlosdev.tmdbapp.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.carlosdev.tmdbapp.databinding.HomeFragmentBinding
import com.carlosdev.tmdbapp.domain.model.OutcomeState
import com.carlosdev.tmdbapp.presentation.home.adapter.MoviesAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModel()
    private val moviesAdapter: MoviesAdapter = MoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        with(binding){
            progressComponent.visibility = View.VISIBLE
            moviesRecyclerView.apply {
                setHasFixedSize(true)
                adapter = moviesAdapter
            }
            swipeRefresh.setOnRefreshListener {
                loadMovies()
                swipeRefresh.isRefreshing = false
            }
        }

        loadMovies()

    }

    private fun loadMovies(){
        binding.progressComponent.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movieRequestState.collect() {outcome ->
                when(outcome) {
                    is OutcomeState.Error -> {
                        binding.progressComponent.visibility = View.GONE
                    }
                    is OutcomeState.Success -> {binding.progressComponent.visibility = View.GONE}
                    is OutcomeState.Progress -> {binding.progressComponent.visibility = View.VISIBLE}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movies.collect{
                val sharedPreferences = context?.getSharedPreferences("preferencias", Context.MODE_PRIVATE)
                var listOfFavorites = sharedPreferences?.getStringSet("favorites", setOf())
                if (listOfFavorites.isNullOrEmpty()){
                    listOfFavorites = setOf()
                }
                moviesAdapter?.updateItems(it, listOfFavorites)
            }
        }
    }

}