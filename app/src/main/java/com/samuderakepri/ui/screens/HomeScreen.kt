package com.samuderakepri.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.ui.MainUiState
import com.samuderakepri.ui.components.EditorialFeaturedCard
import com.samuderakepri.ui.components.HeroHeadlineCard
import com.samuderakepri.ui.components.MaritimeWeatherBanner
import com.samuderakepri.ui.components.MastheadTopBar
import com.samuderakepri.ui.components.NewsArticleCard
import com.samuderakepri.ui.components.OfflineStatusBanner
import com.samuderakepri.ui.components.RubricSectionHeader
import com.samuderakepri.ui.components.ScrollToTopFab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: MainUiState,
    categories: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenArticle: (ArticleEntity) -> Unit,
    onToggleBookmark: (ArticleEntity) -> Unit,
    onOpenLiveWeb: (String) -> Unit,
    onOpenWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    val rubricHeaderLabel = when {
        uiState.searchQuery.isNotBlank() -> "HASIL PENCARIAN"
        uiState.selectedCategory == "Beranda" || uiState.selectedCategory == "Kabar Terkini" ->
            "SEMUA ARTIKEL TERBARU"
        else -> uiState.selectedCategory.uppercase()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MastheadTopBar(
                categories = categories,
                selectedCategory = uiState.selectedCategory,
                searchQuery = uiState.searchQuery,
                breakingArticle = uiState.breakingArticle,
                onSelectCategory = { cat ->
                    onSelectCategory(cat)
                    coroutineScope.launch { listState.scrollToItem(0) }
                },
                onSearchQueryChange = onSearchQueryChange,
                onOpenBreaking = onOpenArticle,
                onOpenLiveWeb = { onOpenLiveWeb("https://www.samuderakepri.co.id") },
                onOpenWhatsApp = onOpenWhatsApp
            )

            if (!uiState.isOnline) {
                OfflineStatusBanner(cachedCount = uiState.allArticles.size)
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_pull_to_refresh")
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 84.dp)
                ) {
                    uiState.heroArticle?.let { hero ->
                        item(key = "hero_${hero.id}") {
                            HeroHeadlineCard(
                                article = hero,
                                onClick = { onOpenArticle(hero) }
                            )
                        }
                    }

                    if (uiState.selectedCategory == "Beranda" && uiState.searchQuery.isBlank()) {
                        item(key = "maritime_weather") {
                            MaritimeWeatherBanner(
                                weather = uiState.maritimeWeather,
                                onOpenBmkgPortal = { onOpenLiveWeb("https://maritim.bmkg.go.id") }
                            )
                        }
                    }

                    item(key = "rubric_header") {
                        RubricSectionHeader(title = rubricHeaderLabel)
                    }

                    if (uiState.filteredArticles.isEmpty() && uiState.heroArticle == null) {
                        item(key = "empty_feed") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Newspaper,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Tidak ada berita pada rubrik atau pencarian ini.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        val isSpecificCategory = uiState.selectedCategory != "Beranda"
                        itemsIndexed(
                            items = uiState.filteredArticles,
                            key = { _, item -> item.id }
                        ) { index, article ->
                            if (isSpecificCategory || index < 2) {
                                EditorialFeaturedCard(
                                    article = article,
                                    onClick = { onOpenArticle(article) },
                                    onToggleBookmark = { onToggleBookmark(article) }
                                )
                            } else {
                                NewsArticleCard(
                                    article = article,
                                    onClick = { onOpenArticle(article) },
                                    onToggleBookmark = { onToggleBookmark(article) }
                                )
                            }
                        }

                        item(key = "offline_footer") {
                            Text(
                                text = "Menampilkan ${uiState.allArticles.size} berita tersimpan di cache lokal untuk akses offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            ScrollToTopFab(
                onClick = {
                    coroutineScope.launch { listState.animateScrollToItem(0) }
                }
            )
        }
    }
}
