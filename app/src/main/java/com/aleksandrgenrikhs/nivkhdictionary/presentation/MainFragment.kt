package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentMainBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by activityViewModels { viewModelFactory }
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

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
        subscribe()
        setClickButton()
    }

    private fun mainToolBar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            return@setOnMenuItemClickListener when (item.itemId) {
                R.id.search -> {
                    viewModel.isSearchViewVisible.value = true
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
                        setReorderingAllowed(true)
                        addToBackStack(null)
                        binding.toolbar.title = getString(R.string.app_name)
                    }
                    binding.toolbar.menu.findItem(R.id.search)?.isVisible = true
                    viewModel.isSearchViewVisible.value = false
                    true
                }

                R.id.favorite -> {
                    val favoriteFragment = FavoritesFragment.newInstance()
                    parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, favoriteFragment)
                        setReorderingAllowed(true)
                        addToBackStack(null)
                        binding.toolbar.title = getString(R.string.favorites)
                    }
                    binding.toolbar.menu.findItem(R.id.search)?.isVisible = true
                    viewModel.isSearchViewVisible.value = false
                    true
                }

                R.id.about -> {
                    val aboutFragment = AboutFragment.newInstance()
                    parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, aboutFragment)
                        setReorderingAllowed(true)
                        addToBackStack(null)
                        binding.toolbar.title = getString(R.string.about)
                    }
                    binding.toolbar.menu.findItem(R.id.search)?.isVisible = false
                    viewModel.isSearchViewVisible.value = false
                    true
                }

                else -> false
            }
        }
    }

    private fun getQuery() {
        binding.searchBar.doAfterTextChanged { text ->
            viewModel.onSearchQuery((text ?: "").toString())
        }
    }

    private fun onBackIconClick() {
        binding.iconBack.setOnClickListener {
            viewModel.isSearchViewVisible.value = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSearchViewVisible.collect {
                if (!it) {
                    binding.searchBar.setText("")
                    viewModel.onSearchQuery("")
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
                }
                binding.layoutSearchView.isVisible = it
                binding.buttonGroup.isVisible = it
            }
        }
    }

    private fun addSymbol1() {
        val newText: String =
            binding.searchBar.text.toString() + "ӷ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol2() {
        val newText: String =
            binding.searchBar.text.toString() + "ғ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol3() {
        val newText: String =
            binding.searchBar.text.toString() + "ӻ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol4() {
        val newText: String =
            binding.searchBar.text.toString() + "ӄ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol5() {
        val newText: String =
            binding.searchBar.text.toString() + "ӈ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol6() {
        val newText: String =
            binding.searchBar.text.toString() + "р̆"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol7() {
        val newText: String =
            binding.searchBar.text.toString() + "ў"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol8() {
        val newText: String =
            binding.searchBar.text.toString() + "ӿ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol9() {
        val newText: String =
            binding.searchBar.text.toString() + "ӽ"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }

    private fun addSymbol10() {
        val newText: String =
            binding.searchBar.text.toString() + "’"
        binding.searchBar.setText(newText)
        binding.searchBar.text?.let { binding.searchBar.setSelection(it.length) }
    }


    private fun setClickButton() {
        binding.letter1.setOnClickListener {
            addSymbol1()
        }
        binding.letter2.setOnClickListener {
            addSymbol2()
        }
        binding.letter3.setOnClickListener {
            addSymbol3()
        }
        binding.letter4.setOnClickListener {
            addSymbol4()
        }
        binding.letter5.setOnClickListener {
            addSymbol5()
        }
        binding.letter6.setOnClickListener {
            addSymbol6()
        }
        binding.letter7.setOnClickListener {
            addSymbol7()
        }
        binding.letter8.setOnClickListener {
            addSymbol8()
        }
        binding.letter9.setOnClickListener {
            addSymbol9()
        }
        binding.letter10.setOnClickListener {
            addSymbol10()
        }
    }
}