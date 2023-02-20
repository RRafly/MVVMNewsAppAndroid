package com.rafly.mvvmnews.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rafly.mvvmnews.R
import com.rafly.mvvmnews.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(val articleItemView: View) : RecyclerView.ViewHolder(articleItemView) {
    }
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        Log.i("ARTICLES","${article.title}, ${article.description}, ${article.source?.name}, ${article.publishedAt}")
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            this.tvTitle.text = article.title
            this.tvDescription.text = article.description
            this.tvSource.text = article.source?.name
            this.tvPublishedAt.text = article.publishedAt

            this.setOnClickListener{
                onItemClickListener?.let { it(article) }
            }
        }
    }


    // agar bisa di panggil di onBind
    private var onItemClickListener: ((Article) -> Unit)? = null
    // supaya bisa di panggil di luar adapter
    fun setOnItemClickListener(listener : (Article) -> Unit) {
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}