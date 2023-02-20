package com.rafly.mvvmnews.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rafly.mvvmnews.R
import com.rafly.mvvmnews.adapters.NewsAdapter
import com.rafly.mvvmnews.ui.NewsActivity
import com.rafly.mvvmnews.ui.NewsViewModel

import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: NewsViewModel
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
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        viewModel.getSavedArticle().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // get pos from rvAdapter.position
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteSavedArticle(article)

                // eh gk jadi
                Snackbar.make(view,"Item Successfuly deleted",Snackbar.LENGTH_LONG).apply {
                    setAction("Revert") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }
        // kita pasang ke rv
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(rvSavedNews)
    }

    private fun setupAdapter() {
        newsAdapter = NewsAdapter()
        // context dari activity
        rvSavedNews.layoutManager = LinearLayoutManager(activity)
        rvSavedNews.adapter = newsAdapter
    }

}