package com.rafly.mvvmnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafly.mvvmnews.R
import com.rafly.mvvmnews.adapters.NewsAdapter
import com.rafly.mvvmnews.ui.NewsActivity
import com.rafly.mvvmnews.ui.NewsViewModel
import com.rafly.mvvmnews.util.Constants
import com.rafly.mvvmnews.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.*


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel

    val TAG = "searchNewsFragment"
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        setupAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                this.putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }
        var job:Job? = null
        // lambada akan di execute jika ada perubahan di et
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        //observe
        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Resource.Success -> { hideProggresBar()
                    response.data?.let { searchResponse ->
                        newsAdapter.differ.submitList(searchResponse.articles)
                        val totalPage = searchResponse.totalResults / Constants.QUERY_PAGE_SIZE
                        islastPage = viewModel.searchNewsPage == totalPage
                        if (islastPage) {
                            rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Loading -> { showProgressBar()}

                is Resource.Error -> {
                    hideProggresBar()
                    response.message?.let {
                        Log.e(TAG , "an error occured : $it")
                        Toast.makeText(activity,it, Toast.LENGTH_LONG).show()}
                }
            }
        }
    }
    var isScrolling = false
    var isLoading = false
    var islastPage = false
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            // scroll detect
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //linearlayout manager agar bisa tahu posisinya
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition()
            Log.i("SCR", "firstVisibleItemPos : $firstVisibleItemPos")
            val visibleItemCount = layoutManager.childCount
            Log.i("SCR","visibleItemCount : $visibleItemCount")
            val totalItemCount = layoutManager.itemCount
            Log.i("SCR", "totalItemCount : $totalItemCount")

            val isNotLoadingAndIsNotLastPage = !isLoading && !islastPage
            val isLastItem = firstVisibleItemPos + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPos > 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndIsNotLastPage && isLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupAdapter() {
        newsAdapter = NewsAdapter()
        // context dari activity
        rvSearchNews.layoutManager = LinearLayoutManager(activity)
        rvSearchNews.adapter = newsAdapter
        rvSearchNews.addOnScrollListener(scrollListener)
    }
    private fun hideProggresBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
}