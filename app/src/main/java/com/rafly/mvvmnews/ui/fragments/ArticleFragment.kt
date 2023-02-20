package com.rafly.mvvmnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.rafly.mvvmnews.R
import com.rafly.mvvmnews.ui.NewsActivity
import com.rafly.mvvmnews.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    // better menggunakan BaseFragment agar tidak menulis berulang
    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // mendapatkan property viewmodel dari activity parentnya
        // cast ke NewsActivity supaya dapat blueprintnya
        viewModel = (activity as NewsActivity).viewModel
        val articles = args.article
        webView.apply {
            // supaya tidak pakai browser bawaan
            this.webViewClient = WebViewClient() // WebViewClient / WebChromeClient
            this.loadUrl(articles.url!!)
        }

        fab.setOnClickListener{
            viewModel.saveArticle(articles)
            Snackbar.make(view, "Article Saved Succesfully",Snackbar.LENGTH_SHORT).show()
        }

    }
}