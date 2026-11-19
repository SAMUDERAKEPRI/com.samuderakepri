package com.samuderakepri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.ui.theme.GoPayGreen
import com.samuderakepri.ui.theme.InvestigativeCrimson
import com.samuderakepri.ui.theme.MaritimeNavy
import com.samuderakepri.ui.theme.MaritimeNavyDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    article: ArticleEntity?,
    fontSizeScale: Float,
    hasGelarFaktaAccess: Boolean,
    onBack: () -> Unit,
    onToggleBookmark: (ArticleEntity) -> Unit,
    onShare: (ArticleEntity) -> Unit,
    onOpenInWebView: (String) -> Unit,
    onOpenWhatsApp: (String) -> Unit,
    onUnlockExclusiveAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAccessDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = article?.category ?: "Berita",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("article_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    if (article != null) {
                        IconButton(
                            onClick = { onToggleBookmark(article) },
                            modifier = Modifier.testTag("detail_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = if (article.isBookmarked) "Hapus dari tersimpan" else "Simpan artikel",
                                tint = if (article.isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { onShare(article) },
                            modifier = Modifier.testTag("detail_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Bagikan Berita"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (article == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Artikel tidak ditemukan di cache lokal.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            return@Scaffold
        }

        val isLocked = article.isExclusive && !hasGelarFaktaAccess
        val paragraphs = article.content.split(Regex("\n\n+")).filter { it.isNotBlank() }
        val visibleParagraphs = if (isLocked && paragraphs.size > 2) {
            paragraphs.take(2)
        } else {
            paragraphs
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            if (article.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
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
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Samudera Kepri • ${article.region}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "${article.category.uppercase()} / ${article.region.uppercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaritimeNavy)
                    ) {
                        Text(
                            text = "SK",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = article.author,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${article.publishedLabel} • ${article.readTimeMinutes} menit baca",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))

                visibleParagraphs.forEachIndexed { index, paragraph ->
                    val alpha = if (isLocked && index == visibleParagraphs.lastIndex) 0.45f else 1f
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (16f * fontSizeScale).sp,
                            lineHeight = (26f * fontSizeScale).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                }

                if (isLocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ExclusivePaywallCard(
                        onGoPayClick = { showAccessDialog = "GoPay / Dukungan Redaksi" },
                        onNftClick = { showAccessDialog = "Verifikasi NFT Pass Samudera Kepri" }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { onOpenInWebView(article.articleUrl) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_article_webview_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Baca Web Asli", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = {
                            val waText =
                                "https://wa.me/6281234567890?text=Halo%20Redaksi%20Samudera%20Kepri%2C%20saya%20menanggapi%20berita%3A%20${article.title}"
                            onOpenWhatsApp(waText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoPayGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("article_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WhatsApp",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    showAccessDialog?.let { method ->
        AlertDialog(
            onDismissRequest = { showAccessDialog = null },
            title = { Text("Akses Laporan Gelar Fakta") },
            text = {
                Text(
                    "Metode dipilih: $method.\n\nAktifkan Akses Penuh pada perangkat ini atau hubungi Redaksi Samudera Kepri via WhatsApp untuk aktivasi keanggotaan resmi."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUnlockExclusiveAccess()
                        showAccessDialog = null
                    },
                    modifier = Modifier.testTag("confirm_unlock_button")
                ) {
                    Text("Aktifkan Akses")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAccessDialog = null
                        onOpenWhatsApp(
                            "https://wa.me/6281234567890?text=Halo%20Redaksi%2C%20saya%20ingin%20aktivasi%20akses%20Gelar%20Fakta%20via%20$method"
                        )
                    }
                ) {
                    Text("Hubungi Redaksi")
                }
            }
        )
    }
}

@Composable
private fun ExclusivePaywallCard(
    onGoPayClick: () -> Unit,
    onNftClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaritimeNavyDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                color = InvestigativeCrimson.copy(alpha = 0.22f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFCA5A5),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "KONTEN EKSKLUSIF GELAR FAKTA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFCA5A5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Buka Kunci Laporan Lengkap",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Laporan investigasi dan data dokumen publik ini disusun khusus oleh Tim Gelar Fakta Samudera Kepri.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGoPayClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoPayGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("paywall_gopay_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Akses Konten via GoPay / Membership",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onNftClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF7C3AED), Color(0xFF2563EB))
                        )
                    )
                    .testTag("paywall_nft_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Token,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verifikasi NFT Pass Samudera Kepri",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
    }
}
