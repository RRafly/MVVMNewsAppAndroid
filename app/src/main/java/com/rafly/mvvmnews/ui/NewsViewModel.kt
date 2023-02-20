package com.rafly.mvvmnews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rafly.mvvmnews.NewsApplication
import com.rafly.mvvmnews.models.Article
import com.rafly.mvvmnews.models.NewsResponse
import com.rafly.mvvmnews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class NewsViewModel(val newsRepository:NewsRepository, app: Application) : AndroidViewModel(app) {

    // ntar coba val breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingPageNumber = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {

       safeBreakingNewsCall(countryCode,breakingPageNumber)
    }


    fun searchNews(searchQuery: String) = viewModelScope.launch {

        safeSearchNewsCall(searchQuery,searchNewsPage)
    }

    // // handle resource and page
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let{ resultResponse ->
                breakingPageNumber++

                if(breakingNewsResponse == null) {
                breakingNewsResponse = resultResponse
                }else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle) // breakingnewsResponse.addAll
                }
                return Resource.Success(breakingNewsResponse)
            }
        }
        return Resource.Error(response.message())
    }
    // handle resource and page
    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let{ resultResponse ->
                searchNewsPage++

                if(searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)//searchNewsResponse.addAll()
                }
                return Resource.Success(searchNewsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedArticle() = newsRepository.getSavedNews()

    fun deleteSavedArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteSavedNews(article)
    }

    // check internet
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
        return false
    }

    private suspend fun safeBreakingNewsCall(countryCode: String,page:Int) {
        breakingNews.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode,page)
                Log.i("RFY",response.body().toString())
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("no internet connection"))
            }
        }catch (e:Throwable) {
            val resource = when(e) {
                is IOException -> Resource.Error<NewsResponse>("Network Failure IO Exception")
                else -> {
                    Resource.Error<NewsResponse>("DATA ERROR")
                }
            }
            breakingNews.postValue(resource)
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String,page:Int) {
        searchNews.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchForNews(searchQuery,page)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("no internet connection"))
            }
        }catch (e:Exception) {
            val resource =  when(e) {
                is IOException -> Resource.Error<NewsResponse>("Network Failure IO Exception")
                else -> Resource.Error<NewsResponse>("DATA ERROR")
            }
            searchNews.postValue(resource)
        }
    }

}