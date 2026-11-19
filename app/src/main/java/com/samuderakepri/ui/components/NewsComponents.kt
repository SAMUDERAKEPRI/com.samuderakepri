package com.samuderakepri.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.samuderakepri.R
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.data.remote.MaritimeWeatherInfo
import com.samuderakepri.ui.theme.CrimsonFabDark
import com.samuderakepri.ui.theme.InvestigativeCrimson
import com.samuderakepri.ui.theme.MaritimeGold
import com.samuderakepri.ui.theme.MaritimeNavy
import com.samuderakepri.ui.theme.MaritimeNavyDark
import com.samuderakepri.ui.theme.OceanCyanBright
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MastheadTopBar(
    categories: List<String>,
    selectedCategory: String,
    searchQuery: String,
    breakingArticle: ArticleEntity?,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenBreaking: (ArticleEntity) -> Unit,
    onOpenLiveWeb: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Strip tanggal & subjudul editorial sesuai screenshot8
        Surface(
            color = MaritimeNavyDark,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1),
                    maxLines = 1
                )
                Text(
                    text = "Portal Berita & Jurnalisme Investigasi Kepri",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    ),
                    color = OceanCyanBright,
                    maxLines = 1
                )
            }
        }

        // Identitas Portal SAMUDERAKEPRI.CO.ID + Search Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_samudera_badge),
                            contentDescription = "Logo Samudera Kepri",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, MaritimeNavy.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SAMUDERAKEPRI.CO.ID",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                color = MaritimeNavy.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "INFORMASI TERPERCAYA & AKURAT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaritimeNavy,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = onOpenLiveWeb,
                            color = MaritimeNavy.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("open_live_web_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = "Buka Portal Web",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Web",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenWhatsApp,
                            modifier = Modifier.testTag("whatsapp_redaksi_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "WhatsApp Redaksi",
                                tint = Color(0xFF00AA5B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Cari berita Kepri, investigasi, hukum...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Hapus pencarian"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Cari berita",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }
        }

        // Bar Kategori Horizontal Gelap (BERANDA, INVESTIGASI [BARU], KABAR TERKINI, dll.)
        Surface(
            color = MaritimeNavyDark,
            contentColor = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { category ->
                    val selected = category == selectedCategory
                    val isInvestigasi = category.equals("Investigasi", ignoreCase = true)
                    Surface(
                        onClick = { onSelectCategory(category) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) Color.White.copy(alpha = 0.16f) else Color.Transparent,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("category_chip_${category.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = if (selected) OceanCyanBright else Color.White
                            )
                            if (isInvestigasi) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = InvestigativeCrimson,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "BARU",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Strip BREAKING News di bawah bar kategori sesuai screenshot8
        breakingArticle?.let { breaking ->
            Surface(
                color = Color(0xFF1E293B),
                contentColor = Color.White,
                onClick = { onOpenBreaking(breaking) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("breaking_news_strip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Surface(
                        color = InvestigativeCrimson,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "BREAKING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = breaking.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFFF1F5F9),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun RubricSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.weight(1f)
        )
        Surface(
            color = InvestigativeCrimson,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EditorialFeaturedCard(
    article: ArticleEntity,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("featured_card_${article.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (article.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.imageUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = article.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Water,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                if (article.isExclusive) {
                    Surface(
                        color = InvestigativeCrimson,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Eksklusif",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EKSKLUSIF",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = article.region.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = InvestigativeCrimson
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = article.publishedLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("featured_bookmark_${article.id}")
                ) {
                    Icon(
                        imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (article.isBookmarked) "Hapus dari tersimpan" else "Simpan artikel",
                        tint = if (article.isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 17.sp,
                    lineHeight = 23.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = article.excerpt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ScrollToTopFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = CrimsonFabDark,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        modifier = modifier
            .size(52.dp)
            .testTag("scroll_to_top_button")
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = "Kembali ke atas"
        )
    }
}

@Composable
fun OfflineStatusBanner(
    cachedCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF1E293B),
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.WifiOff,
                contentDescription = "Mode Offline",
                tint = MaritimeGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mode Offline (Blank Spot): Menampilkan $cachedCount berita tersimpan di cache lokal.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFF1F5F9)
            )
        }
    }
}

@Composable
fun HeroHeadlineCard(
    article: ArticleEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable(onClick = onClick)
            .testTag("hero_headline_card")
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
                .background(MaritimeNavyDark)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (article.isInvestigative) InvestigativeCrimson else MaritimeNavy,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = article.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = article.region,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${article.publishedLabel} • ${article.readTimeMinutes} Menit Baca",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
fun MaritimeWeatherBanner(
    weather: MaritimeWeatherInfo,
    onOpenBmkgPortal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpenBmkgPortal,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("maritime_weather_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(MaritimeNavy, MaritimeNavyDark)
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CloudQueue,
                            contentDescription = "Cuaca Maritim",
                            tint = MaritimeGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INFO CUACA MARITIM KEPRI",
                            style = MaterialTheme.typography.labelSmall,
                            color = OceanCyanBright
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = weather.sourceLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = OceanCyanBright,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${weather.areaName}: ${weather.condition}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = weather.windInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBFDBFE)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = weather.temperature,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaritimeGold
                    )
                }
            }
        }
    }
}

@Composable
fun NewsArticleCard(
    article: ArticleEntity,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("article_card_${article.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (article.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.imageUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = article.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Water,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Surface(
                    color = if (article.isInvestigative) InvestigativeCrimson else MaritimeNavy.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (article.isExclusive) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Laporan Eksklusif",
                            tint = InvestigativeCrimson,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "EKSKLUSIF",
                            style = MaterialTheme.typography.labelSmall,
                            color = InvestigativeCrimson
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.excerpt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${article.publishedLabel} • ${article.region}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("bookmark_button_${article.id}")
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (article.isBookmarked) "Hapus dari tersimpan" else "Simpan artikel",
                            tint = if (article.isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
