package com.samuderakepri.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.ui.MainUiState
import com.samuderakepri.ui.components.NewsArticleCard
import com.samuderakepri.ui.theme.MaritimeNavy
import com.samuderakepri.ui.theme.OceanCyanBright

@Composable
fun SavedScreen(
    uiState: MainUiState,
    onOpenArticle: (ArticleEntity) -> Unit,
    onToggleBookmark: (ArticleEntity) -> Unit,
    onSyncOffline: () -> Unit,
    onClearNonBookmarkedCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaritimeNavy,
            contentColor = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = OceanCyanBright,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TERSIMPAN & CACHE OFFLINE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Akses berita tersimpan di memori perangkat meski sedang melaut atau berada di area blank spot.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "${uiState.allArticles.size} artikel siap dibaca tanpa sinyal • ${uiState.bookmarkedArticles.size} ditandai penting",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onSyncOffline,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("sync_offline_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sinkronkan", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = onClearNonBookmarkedCache,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("clear_cache_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bersihkan Cache", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Ditandai (${uiState.bookmarkedArticles.size})") },
                modifier = Modifier.testTag("saved_tab_bookmarks")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Semua Cache (${uiState.allArticles.size})") },
                modifier = Modifier.testTag("saved_tab_all_cache")
            )
        }

        val displayedList = if (selectedTab == 0) uiState.bookmarkedArticles else uiState.allArticles

        if (displayedList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bookmarks,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (selectedTab == 0) {
                        "Belum ada artikel yang ditandai.\nKetuk ikon Simpan pada berita mana saja untuk menyimpan permanen."
                    } else {
                        "Cache lokal masih kosong. Ketuk Sinkronkan saat terhubung ke internet."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(
                    items = displayedList,
                    key = { it.id }
                ) { article ->
                    NewsArticleCard(
                        article = article,
                        onClick = { onOpenArticle(article) },
                        onToggleBookmark = { onToggleBookmark(article) }
                    )
                }
            }
        }
    }
}
