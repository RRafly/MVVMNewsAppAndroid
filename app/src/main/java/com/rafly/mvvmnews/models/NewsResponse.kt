package com.rafly.mvvmnews.models

import com.rafly.mvvmnews.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)