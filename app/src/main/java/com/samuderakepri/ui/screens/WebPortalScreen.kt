package com.samuderakepri.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.samuderakepri.ui.components.OfflineStatusBanner
import com.samuderakepri.ui.theme.MaritimeNavy
import com.samuderakepri.ui.theme.OceanCyanBright
import com.samuderakepri.utils.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPortalScreen(
    initialUrl: String,
    isOnline: Boolean,
    cachedArticlesCount: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pageTitle by remember { mutableStateOf("Samudera Kepri Web") }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var hasMainError by remember { mutableStateOf(false) }

    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    BackHandler(enabled = true) {
        val webView = webViewRef.value
        if (webView != null && webView.canGoBack()) {
            webView.goBack()
        } else {
            onClose()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef.value?.destroy()
            webViewRef.value = null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = pageTitle,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFBFDBFE),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val webView = webViewRef.value
                                if (webView != null && webView.canGoBack()) {
                                    webView.goBack()
                                } else {
                                    onClose()
                                }
                            },
                            modifier = Modifier.testTag("webview_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { IntentUtils.shareArticle(context, pageTitle, currentUrl) },
                            modifier = Modifier.testTag("webview_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Bagikan halaman"
                            )
                        }
                        IconButton(
                            onClick = {
                                hasMainError = false
                                webViewRef.value?.reload()
                            },
                            modifier = Modifier.testTag("webview_refresh_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Muat Ulang"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaritimeNavy,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )

                if (isLoading && progress in 1..99) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        color = OceanCyanBright,
                        trackColor = MaritimeNavy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (!isOnline) {
                    OfflineStatusBanner(cachedCount = cachedArticlesCount)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("live_webview"),
                factory = { ctx ->
                    SwipeRefreshLayout(ctx).apply {
                        setColorSchemeColors(
                            AndroidColor.parseColor("#003366"),
                            AndroidColor.parseColor("#00C9A7")
                        )

                        val webView = WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            configureWebViewSettings(isOnline)

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: Bitmap?
                                ) {
                                    isLoading = true
                                    hasMainError = false
                                    if (!url.isNullOrBlank()) currentUrl = url
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                    isRefreshing = false
                                    if (!url.isNullOrBlank()) currentUrl = url
                                    view?.title?.takeIf { it.isNotBlank() && !it.startsWith("http") }
                                        ?.let { pageTitle = it }
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (request?.isForMainFrame == true) {
                                        isLoading = false
                                        isRefreshing = false
                                        hasMainError = true
                                    }
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    return handleUrlNavigation(ctx, url)
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    progress = newProgress
                                    if (newProgress >= 100) {
                                        isLoading = false
                                        isRefreshing = false
                                    }
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrBlank() && !title.startsWith("http")) {
                                        pageTitle = title
                                    }
                                }
                            }

                            loadUrl(initialUrl)
                        }

                        webViewRef.value = webView
                        addView(webView)

                        setOnChildScrollUpCallback { _, _ ->
                            webView.scrollY > 0
                        }

                        setOnRefreshListener {
                            if (!isOnline) {
                                Toast.makeText(
                                    ctx,
                                    "Perangkat offline. Menampilkan cache lokal jika tersedia.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isRefreshing = false
                            } else {
                                hasMainError = false
                                webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
                                webView.reload()
                            }
                        }
                    }
                },
                update = { swipeLayout ->
                    val wv = webViewRef.value
                    wv?.settings?.cacheMode = if (isOnline) {
                        WebSettings.LOAD_DEFAULT
                    } else {
                        WebSettings.LOAD_CACHE_ELSE_NETWORK
                    }
                    swipeLayout.visibility = View.VISIBLE
                }
            )

            if (hasMainError) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.WifiOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Halaman belum tersedia di cache offline atau koneksi terputus.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            hasMainError = false
                            webViewRef.value?.reload()
                        },
                        modifier = Modifier.testTag("webview_retry_button")
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.configureWebViewSettings(isOnline: Boolean) {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        loadsImagesAutomatically = true
        useWideViewPort = true
        loadWithOverviewMode = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        allowFileAccess = false
        allowContentAccess = false
        cacheMode = if (isOnline) WebSettings.LOAD_DEFAULT else WebSettings.LOAD_CACHE_ELSE_NETWORK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
    }
}

private fun handleUrlNavigation(context: android.content.Context, url: String): Boolean {
    val uri = Uri.parse(url)
    val scheme = uri.scheme?.lowercase() ?: ""

    if (IntentUtils.isWhatsAppUrl(url)) {
        IntentUtils.openWhatsApp(context, url)
        return true
    }

    if (scheme == "tel") {
        try {
            context.startActivity(Intent(Intent.ACTION_DIAL, uri))
        } catch (_: Exception) {
            Toast.makeText(context, "Tidak dapat membuka dialer telepon", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    if (scheme == "mailto") {
        try {
            context.startActivity(Intent(Intent.ACTION_SENDTO, uri))
        } catch (_: Exception) {
            Toast.makeText(context, "Tidak dapat membuka aplikasi email", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    if (IntentUtils.isInternalSamuderaUrl(url) || uri.host.equals("maritim.bmkg.go.id", ignoreCase = true)) {
        return false
    }

    IntentUtils.openExternalBrowser(context, url)
    return true
}
