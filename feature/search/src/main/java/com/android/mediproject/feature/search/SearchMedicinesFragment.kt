package com.android.mediproject.feature.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.android.mediproject.core.common.viewmodel.UiState
import com.android.mediproject.core.ui.base.BaseFragment
import com.android.mediproject.core.ui.base.view.MediSearchbar
import com.android.mediproject.feature.search.databinding.FragmentSearchMedicinesBinding
import com.android.mediproject.feature.search.recentsearchlist.RecentSearchListFragment
import com.android.mediproject.feature.search.recentsearchlist.RecentSearchListFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchMedicinesFragment :
    BaseFragment<FragmentSearchMedicinesBinding, SearchMedicinesViewModel>(FragmentSearchMedicinesBinding::inflate) {

    override val fragmentViewModel by viewModels<SearchMedicinesViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // contents_fragment_container_view 에 최근 검색 목록과 검색 결과 목록 화면 두 개를 띄운다.
        initSearchBar()

        arguments?.apply {
            getString("searchQuery")?.also {
                binding.searchView.searchWithQuery(query = it)
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentViewModel.searchResult.collectLatest { state ->
                    if (state == UiState.isLoading) {

                        binding.contentsFragmentContainerView.findNavController()
                            .navigate(RecentSearchListFragmentDirections.actionRecentSearchListFragmentToManualSearchResultFragment())
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.apply {
            childFragmentManager.findFragmentById(R.id.contents_fragment_container_view)?.also { navHostFragment ->
                navHostFragment.childFragmentManager.apply {
                    setFragmentResultListener(
                        RecentSearchListFragment.ResultKey.RESULT_KEY.name, navHostFragment.viewLifecycleOwner
                    ) { _, bundle ->

                        bundle.apply {
                            getString(RecentSearchListFragment.ResultKey.WORD.name)?.also {
                                binding.searchView.searchWithQuery(query = it)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 검색바 검색 가능하도록 설정하고, 검색버튼과 AI검색 버튼이 동작하도록 초기화합니다.
     */
    private fun initSearchBar() {
        binding.searchView.setOnSearchAiBtnClickListener {}

        binding.searchView.setOnSearchBtnClickListener(object : MediSearchbar.SearchQueryCallback {
            override fun onSearchQuery(query: String) {
                fragmentViewModel.searchMedicines(query)
            }

            override fun onEmptyQuery() {
                toast(getString(com.android.mediproject.core.ui.R.string.search_empty_query))
            }
        })
    }
}