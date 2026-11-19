package com.samuderakepri.data.repository

import com.samuderakepri.data.local.ArticleDao
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.data.local.NewsSeedData
import com.samuderakepri.data.remote.MaritimeWeatherInfo
import com.samuderakepri.data.remote.NewsRemoteDataSource
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val articleDao: ArticleDao,
    private val remoteDataSource: NewsRemoteDataSource
) {

    val allArticles: Flow<List<ArticleEntity>> = articleDao.getAllArticles()
    val investigativeArticles: Flow<List<ArticleEntity>> = articleDao.getInvestigativeArticles()
    val bookmarkedArticles: Flow<List<ArticleEntity>> = articleDao.getBookmarkedArticles()

    fun getArticleById(id: String): Flow<ArticleEntity?> = articleDao.getArticleById(id)

    suspend fun ensureInitialCache() {
        if (articleDao.getArticleCount() == 0) {
            articleDao.upsertArticles(NewsSeedData.getInitialCachedArticles())
        }
    }

    suspend fun syncLatestArticles(): Result<Int> {
        val result = remoteDataSource.fetchLiveArticles()
        return upsertSyncedResult(result)
    }

    suspend fun syncCategoryArticles(categoryName: String): Result<Int> {
        val result = remoteDataSource.fetchCategoryArticles(categoryName)
        return upsertSyncedResult(result)
    }

    private suspend fun upsertSyncedResult(result: Result<List<ArticleEntity>>): Result<Int> {
        return result.map { remoteArticles ->
            val bookmarkedIds = articleDao.getBookmarkedIds().toSet()
            val merged = remoteArticles.map { article ->
                article.copy(isBookmarked = article.id in bookmarkedIds)
            }
            if (merged.isNotEmpty()) {
                articleDao.upsertArticles(merged)
            }
            merged.size
        }
    }

    suspend fun toggleBookmark(articleId: String, currentlyBookmarked: Boolean) {
        articleDao.updateBookmark(articleId, !currentlyBookmarked)
    }

    suspend fun clearNonBookmarkedCache() {
        articleDao.clearNonBookmarkedCache()
    }

    suspend fun fetchMaritimeWeather(): MaritimeWeatherInfo {
        return remoteDataSource.fetchMaritimeWeather()
    }
}
