package com.rafly.mvvmnews.ui


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rafly.mvvmnews.NewsApplication

class NewsViewModelProviderFactory(val newsRepository: NewsRepository,val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository,application) as T
    }
}