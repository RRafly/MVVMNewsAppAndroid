package com.rafly.mvvmnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rafly.mvvmnews.NewsApplication
import com.rafly.mvvmnews.R
import com.rafly.mvvmnews.db.ArticleDatabase
import kotlinx.android.synthetic.main.activity_news.*


class NewsActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)



        val database = ArticleDatabase(this)
        val repository = NewsRepository(database)
        viewModel = ViewModelProvider(viewModelStore,NewsViewModelProviderFactory(repository,
            application
        ))
            .get(NewsViewModel::class.java)


        // tadinya bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController()) trus diganti dan sekarang workk hya hya
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }
}