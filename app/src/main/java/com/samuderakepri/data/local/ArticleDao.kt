package com.samuderakepri.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY isHero DESC, publishedTimestamp DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isInvestigative = 1 OR category = 'Investigasi' OR category = 'Hukum & Kriminal' ORDER BY publishedTimestamp DESC")
    fun getInvestigativeArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY cachedAt DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :articleId LIMIT 1")
    fun getArticleById(articleId: String): Flow<ArticleEntity?>

    @Query("SELECT id FROM articles WHERE isBookmarked = 1")
    suspend fun getBookmarkedIds(): List<String>

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticleCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticle(article: ArticleEntity)

    @Query("UPDATE articles SET isBookmarked = :bookmarked WHERE id = :articleId")
    suspend fun updateBookmark(articleId: String, bookmarked: Boolean)

    @Query("DELETE FROM articles WHERE isBookmarked = 0")
    suspend fun clearNonBookmarkedCache()
}
