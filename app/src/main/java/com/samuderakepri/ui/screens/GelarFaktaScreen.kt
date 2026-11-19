package com.samuderakepri.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.ui.MainUiState
import com.samuderakepri.ui.components.OfflineStatusBanner
import com.samuderakepri.ui.components.ScrollToTopFab
import com.samuderakepri.ui.theme.InvestigativeCrimson
import com.samuderakepri.ui.theme.MaritimeGold
import com.samuderakepri.ui.theme.MaritimeNavyDark
import com.samuderakepri.ui.theme.OceanCyanBright
import kotlinx.coroutines.launch

@Composable
fun GelarFaktaScreen(
    uiState: MainUiState,
    onOpenArticle: (ArticleEntity) -> Unit,
    onToggleBookmark: (ArticleEntity) -> Unit,
    onSendTipWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    val spotlightArticle = uiState.investigativeArticles.firstOrNull()
    val secondaryArticles = if (spotlightArticle != null) {
        uiState.investigativeArticles.drop(1)
    } else {
        emptyList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaritimeNavyDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header gelap elegan sesuai screenshot2: INVESTIGASI • GELAR FAKTA
            Surface(
                color = MaritimeNavyDark,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "INVESTIGASI  •  GELAR FAKTA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.1.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Telaah dokumen publik, liputan hukum, dan laporan investigasi mendalam Kepulauan Riau.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            if (!uiState.isOnline) {
                OfflineStatusBanner(cachedCount = uiState.allArticles.size)
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                spotlightArticle?.let { spotlight ->
                    item(key = "gelar_fakta_spotlight") {
                        GelarFaktaSpotlightCard(
                            article = spotlight,
                            onClick = { onOpenArticle(spotlight) }
                        )
                    }
                }

                item(key = "gelar_fakta_section_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = InvestigativeCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GELAR FAKTA TERBARU",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(
                            thickness = 1.5.dp,
                            color = InvestigativeCrimson
                        )
                    }
                }

                items(
                    items = secondaryArticles,
                    key = { it.id }
                ) { article ->
                    GelarFaktaDarkRowCard(
                        article = article,
                        onClick = { onOpenArticle(article) },
                        onToggleBookmark = { onToggleBookmark(article) }
                    )
                }

                item(key = "dossier_whistleblower_banner") {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF17253B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = InvestigativeCrimson,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "KANAL PENGADUAN PUBLIK",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                if (uiState.userPreferences.hasGelarFaktaAccess) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.VerifiedUser,
                                            contentDescription = "Akses Aktif",
                                            tint = OceanCyanBright,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Akses Penuh Aktif",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OceanCyanBright
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Punya Dokumen atau Fakta Lapangan di Kepri?",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Redaksi melindungi identitas narasumber sesuai UU Pers. Kirim informasi langsung ke meja redaksi.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onSendTipWhatsApp,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00AA5B),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("gelar_fakta_whatsapp_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Kirim Laporan ke Redaksi (WhatsApp)",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
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

@Composable
private fun GelarFaktaSpotlightCard(
    article: ArticleEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(215.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("gelar_fakta_spotlight_card")
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(article.imageUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E293B))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),
                            Color(0xFF0F172A).copy(alpha = 0.85f),
                            Color(0xFF0F172A).copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "${article.region.uppercase()}, SAMUDERAKEPRI —",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaritimeGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 21.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Baca Selengkapnya",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun GelarFaktaDarkRowCard(
    article: ArticleEntity,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color(0xFF162235),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("gelar_fakta_card_${article.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.imageUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(104.dp)
                    .height(78.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GELAR FAKTA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.6.sp
                    ),
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 19.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = article.publishedLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }

            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("gelar_fakta_bookmark_${article.id}")
            ) {
                Icon(
                    imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (article.isBookmarked) "Hapus dari tersimpan" else "Simpan artikel",
                    tint = if (article.isBookmarked) MaritimeGold else Color(0xFF94A3B8)
                )
            }
        }
    }
}
