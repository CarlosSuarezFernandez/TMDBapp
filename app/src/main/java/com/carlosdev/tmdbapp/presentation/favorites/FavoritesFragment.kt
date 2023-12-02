package com.carlosdev.tmdbapp.presentation.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.carlosdev.tmdbapp.databinding.FragmentFavoritesBinding
import com.carlosdev.tmdbapp.databinding.HomeFragmentBinding
import com.carlosdev.tmdbapp.domain.model.OutcomeState
import com.carlosdev.tmdbapp.presentation.favorites.adapter.FavoritesAdapter
import com.carlosdev.tmdbapp.presentation.home.HomeViewModel
import com.carlosdev.tmdbapp.presentation.home.adapter.MoviesAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()
    private val favoritesAdapter: FavoritesAdapter = FavoritesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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

        with(binding){
            progressComponent.visibility = View.VISIBLE
            moviesRecyclerView.apply {
                setHasFixedSize(true)
                adapter = favoritesAdapter
            }
            swipeRefresh.setOnRefreshListener {
                loadMovies()
                swipeRefresh.isRefreshing = false
            }
        }
        initPreferencesViewModel()
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
                favoritesAdapter.updateItems(it, listOfFavorites)
            }
        }
    }

    private fun initPreferencesViewModel(){
        viewModel.initSharedPreferences(requireContext())
    }

}