package com.samuderakepri.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val excerpt: String,
    val content: String,
    val category: String,
    val region: String,
    val imageUrl: String,
    val articleUrl: String,
    val author: String,
    val publishedLabel: String,
    val publishedTimestamp: Long,
    val readTimeMinutes: Int = 4,
    val isHero: Boolean = false,
    val isInvestigative: Boolean = false,
    val isExclusive: Boolean = false,
    val isBookmarked: Boolean = false,
    val isCachedOffline: Boolean = true,
    val cachedAt: Long = System.currentTimeMillis()
)
