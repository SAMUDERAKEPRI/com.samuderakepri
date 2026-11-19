package com.samuderakepri.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samuderakepri.BuildConfig
import com.samuderakepri.R
import com.samuderakepri.ui.MainUiState
import com.samuderakepri.ui.theme.MaritimeNavy
import com.samuderakepri.ui.theme.OceanCyanBright

@Composable
fun ProfileScreen(
    uiState: MainUiState,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSelectFontScale: (Float) -> Unit,
    onToggleAutoCache: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onOpenLiveWeb: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onShareApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs = uiState.userPreferences
    var showPosterDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaritimeNavy,
            contentColor = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_samudera_official),
                    contentDescription = "Logo PT Media Samudera Kepri",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pembaca Samudera Kepri",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (prefs.hasGelarFaktaAccess) {
                            "Akses Eksklusif Gelar Fakta Aktif"
                        } else {
                            "Pembaca Reguler • Mode Offline Siap"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = OceanCyanBright
                    )
                }
                if (prefs.hasGelarFaktaAccess) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = "Terverifikasi",
                        tint = OceanCyanBright
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "PROMOSI & IDENTITAS RESMI",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    onClick = onOpenLiveWeb,
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_banner_card")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_samudera_official),
                        contentDescription = "Banner Resmi Aplikasi Samudera Kepri",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1024f / 500f)
                    )
                }
            }

            item {
                Card(
                    onClick = { showPosterDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_poster_card")
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.poster_samudera_official),
                            contentDescription = "Poster Resmi Samudera Kepri",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Poster Aplikasi Resmi Samudera Kepri",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Ketuk untuk pratinjau ukuran penuh atau bagikan ke rekan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "PREFERENSI MEMBACA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        PreferenceSwitchRow(
                            icon = Icons.Filled.DarkMode,
                            title = "Mode Gelap (Nyaman di Laut Malam)",
                            subtitle = "Mengurangi silau layar saat membaca di kondisi minim cahaya",
                            checked = prefs.isDarkTheme,
                            onCheckedChange = onToggleDarkTheme,
                            testTag = "switch_dark_theme"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        PreferenceSwitchRow(
                            icon = Icons.Filled.OfflinePin,
                            title = "Simpan Cache Otomatis",
                            subtitle = "Unduh berita terbaru otomatis saat terhubung Wi-Fi/Seluler",
                            checked = prefs.autoCacheOffline,
                            onCheckedChange = onToggleAutoCache,
                            testTag = "switch_auto_cache"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        PreferenceSwitchRow(
                            icon = Icons.Filled.Notifications,
                            title = "Notifikasi Breaking News Kepri",
                            subtitle = "Terima sorotan berita investigasi dan peringatan cuaca maritim",
                            checked = prefs.notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            testTag = "switch_notifications"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.TextFields,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Ukuran Teks Artikel",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val options = listOf(0.9f to "Kecil", 1.0f to "Normal", 1.2f to "Besar")
                                options.forEach { (scale, label) ->
                                    FilterChip(
                                        selected = kotlin.math.abs(prefs.fontSizeScale - scale) < 0.05f,
                                        onClick = { onSelectFontScale(scale) },
                                        label = { Text(label) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("font_chip_${label.lowercase()}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "LAYANAN REDAKSI & PORTAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ActionRow(
                            icon = Icons.Filled.Language,
                            title = "Buka Portal Web Resmi",
                            subtitle = "https://www.samuderakepri.co.id (WebView)",
                            onClick = onOpenLiveWeb,
                            testTag = "profile_open_web_button"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        ActionRow(
                            icon = Icons.Outlined.Chat,
                            title = "Hubungi Redaksi via WhatsApp",
                            subtitle = "Kirim pengaduan, hak jawab, atau kerja sama iklan",
                            onClick = onOpenWhatsApp,
                            testTag = "profile_whatsapp_button"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        ActionRow(
                            icon = Icons.Filled.Share,
                            title = "Bagikan Aplikasi Samudera Kepri",
                            subtitle = "Ajak rekan & sesama pembaca di Kepulauan Riau",
                            onClick = onShareApp,
                            testTag = "profile_share_app_button"
                        )
                    }
                }
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "PT MEDIA SAMUDERA KEPRI",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Versi ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE}) • ${BuildConfig.APPLICATION_ID}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showPosterDialog) {
        AlertDialog(
            onDismissRequest = { showPosterDialog = false },
            title = { Text("Samudera Kepri Resmi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.poster_samudera_official),
                        contentDescription = "Poster Resmi Samudera Kepri",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Text(
                        text = "Portal berita terkini & jurnalisme investigasi Kepulauan Riau. Akses cepat, ringan, dan siap baca offline.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showPosterDialog = false
                    onShareApp()
                }) {
                    Text("Bagikan Aplikasi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPosterDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
