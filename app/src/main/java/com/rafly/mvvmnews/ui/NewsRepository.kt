package com.rafly.mvvmnews.ui

import com.rafly.mvvmnews.api.RetrofitInstance
import com.rafly.mvvmnews.db.ArticleDatabase
import com.rafly.mvvmnews.models.Article

class NewsRepository(private val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode:String,page:Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,page)

    suspend fun searchForNews(searchQuery:String,page: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,page)

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    // tidak di suspend fun karena akan di akses di liveData
    fun getSavedNews() =
        db.getArticleDao().getAllArticles()

    suspend fun deleteSavedNews(article: Article) =
        db.getArticleDao().deleteArticle(article)
}