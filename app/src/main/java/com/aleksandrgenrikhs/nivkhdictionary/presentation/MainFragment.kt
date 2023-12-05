package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentMainBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.MainViewModelFactory
import javax.inject.Inject

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    lateinit var searchView: SearchView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as ComponentProvider).provideComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.enableEdgeToEdge()
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation()
        mainToolBar()
        onBackIconClick()
        getQuery()
    }

    private fun mainToolBar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            return@setOnMenuItemClickListener when (item.itemId) {
                R.id.search -> {
                    binding.layoutSearchView.visibility = View.VISIBLE
                    true
                }

                else -> false
            }
        }
    }

    private fun bottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            return@setOnItemSelectedListener when (menuItem.itemId) {
                R.id.home -> {
                    val homeFragment = HomeFragment.newInstance()
                    parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, homeFragment)
                        addToBackStack(null)
                        binding.toolbar.title = getString(R.string.app_name)
                    }
                    binding.toolbar.menu.findItem(R.id.search)?.isVisible = true
                    binding.layoutSearchView.visibility = View.GONE
                    true
                }

                R.id.favorite -> {
                    val favoriteFragment = FavoritesFragment.newInstance()
                    parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, favoriteFragment)
                        addToBackStack(null)
                        binding.toolbar.title = getString(R.string.favorites)
                    }
                    binding.toolbar.menu.findItem(R.id.search)?.isVisible = true
                    binding.layoutSearchView.visibility = View.GONE
                    true
                }

                R.id.about -> {
                    val aboutFragment = AboutFragment.newInstance()
                    parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, aboutFragment)
                        addToBackStack(null)
                        binding.toolbar.title = getString(R.string.about)
                    }
                    binding.toolbar.menu.findItem(R.id.search)?.isVisible = false
                    binding.layoutSearchView.visibility = View.GONE
                    true
                }

                else -> false
            }
        }
    }

    private fun getQuery() {
        searchView = binding.searchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.onSearchQuery(newText)
                }
                return true
            }

        })
    }

    private fun onBackIconClick() {
        binding.ivSearchView.setOnClickListener {
            binding.layoutSearchView.visibility = View.GONE

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}