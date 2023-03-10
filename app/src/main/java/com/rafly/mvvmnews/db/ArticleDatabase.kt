package com.rafly.mvvmnews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rafly.mvvmnews.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao():ArticleDao

    companion object{
        var instance:ArticleDatabase? = null
        var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context)
            }

        private fun createDatabase(context: Context):ArticleDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
        }
    }
}