package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private lateinit var viewModel: MainViewModel

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
    }

    private fun mainToolBar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            return@setOnMenuItemClickListener when (item.itemId) {
                R.id.search -> {
                    Toast.makeText(requireContext(), "Search Click", Toast.LENGTH_LONG).show()
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
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}