package com.samuderakepri

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samuderakepri.ui.MainViewModel
import com.samuderakepri.ui.screens.ArticleDetailScreen
import com.samuderakepri.ui.screens.GelarFaktaScreen
import com.samuderakepri.ui.screens.HomeScreen
import com.samuderakepri.ui.screens.ProfileScreen
import com.samuderakepri.ui.screens.SavedScreen
import com.samuderakepri.ui.screens.WebPortalScreen
import com.samuderakepri.ui.theme.SamuderaKepriTheme
import com.samuderakepri.utils.IntentUtils

private sealed class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Beranda : BottomTab("home", "Beranda", Icons.Filled.Home, Icons.Outlined.Home)
    data object GelarFakta : BottomTab("gelar_fakta", "Gelar Fakta", Icons.Filled.Gavel, Icons.Outlined.Gavel)
    data object Tersimpan : BottomTab("saved", "Tersimpan", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    data object Profil : BottomTab("profile", "Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {

    companion object {
        const val NEWS_URL = "https://www.samuderakepri.co.id"
        private const val ROUTE_ARTICLE = "article/{articleId}"
        private const val ROUTE_WEBVIEW = "webview?url={url}"
        private const val BACK_PRESS_INTERVAL_MS = 2000L
    }

    private var deepLinkUrlState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkUrlState.value = extractDeepLink(intent)

        setContent {
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            SamuderaKepriTheme(darkTheme = uiState.userPreferences.isDarkTheme) {
                SamuderaKepriApp(
                    viewModel = viewModel,
                    pendingDeepLink = deepLinkUrlState.value,
                    onDeepLinkConsumed = { deepLinkUrlState.value = null },
                    onExitApp = { finish() }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        extractDeepLink(intent)?.let { url ->
            deepLinkUrlState.value = url
        }
    }

    private fun extractDeepLink(intent: Intent?): String? {
        val data = intent?.data ?: return null
        val url = data.toString()
        return if (url.startsWith("http")) url else null
    }

    @Composable
    private fun SamuderaKepriApp(
        viewModel: MainViewModel,
        pendingDeepLink: String?,
        onDeepLinkConsumed: () -> Unit,
        onExitApp: () -> Unit
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val context = LocalContext.current
        val snackbarHostState = remember { SnackbarHostState() }
        var lastBackPressTime by remember { mutableLongStateOf(0L) }

        val topLevelTabs = remember {
            listOf(
                BottomTab.Beranda,
                BottomTab.GelarFakta,
                BottomTab.Tersimpan,
                BottomTab.Profil
            )
        }

        val showBottomNav = topLevelTabs.any { tab ->
            currentDestination?.hierarchy?.any { it.route == tab.route } == true
        }

        val isOnHomeRoot = currentDestination?.route == BottomTab.Beranda.route
        BackHandler(enabled = isOnHomeRoot) {
            val now = System.currentTimeMillis()
            if (now - lastBackPressTime < BACK_PRESS_INTERVAL_MS) {
                onExitApp()
            } else {
                lastBackPressTime = now
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_exit_confirm),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        LaunchedEffect(pendingDeepLink) {
            if (!pendingDeepLink.isNullOrBlank()) {
                navController.navigate("webview?url=${Uri.encode(pendingDeepLink)}")
                onDeepLinkConsumed()
            }
        }

        LaunchedEffect(uiState.statusBannerMessage) {
            val msg = uiState.statusBannerMessage
            if (msg != null) {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearStatusMessage()
            }
        }

        val navigateToTab: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        val openArticle: (String) -> Unit = { articleId ->
            navController.navigate("article/${Uri.encode(articleId)}")
        }

        val openLiveWeb: (String) -> Unit = { url ->
            navController.navigate("webview?url=${Uri.encode(url)}")
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp && showBottomNav

            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (showBottomNav && !useNavigationRail) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            topLevelTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = { Text(tab.label) },
                                    modifier = Modifier.testTag("nav_${tab.route}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavigationRail) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            topLevelTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = { Text(tab.label) },
                                    modifier = Modifier.testTag("rail_${tab.route}")
                                )
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = BottomTab.Beranda.route,
                        modifier = Modifier.weight(1f)
                    ) {
                        composable(BottomTab.Beranda.route) {
                            HomeScreen(
                                uiState = uiState,
                                categories = viewModel.categories,
                                onSearchQueryChange = viewModel::updateSearchQuery,
                                onSelectCategory = viewModel::selectCategory,
                                onRefresh = { viewModel.refreshNews(silent = false) },
                                onOpenArticle = { article -> openArticle(article.id) },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onOpenLiveWeb = openLiveWeb,
                                onOpenWhatsApp = { IntentUtils.openWhatsApp(context) }
                            )
                        }

                        composable(BottomTab.GelarFakta.route) {
                            GelarFaktaScreen(
                                uiState = uiState,
                                onOpenArticle = { article -> openArticle(article.id) },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onSendTipWhatsApp = { IntentUtils.openWhatsApp(context) }
                            )
                        }

                        composable(BottomTab.Tersimpan.route) {
                            SavedScreen(
                                uiState = uiState,
                                onOpenArticle = { article -> openArticle(article.id) },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onSyncOffline = { viewModel.refreshNews(silent = false) },
                                onClearNonBookmarkedCache = viewModel::clearNonBookmarkedCache
                            )
                        }

                        composable(BottomTab.Profil.route) {
                            ProfileScreen(
                                uiState = uiState,
                                onToggleDarkTheme = viewModel::setDarkTheme,
                                onSelectFontScale = viewModel::setFontSizeScale,
                                onToggleAutoCache = viewModel::setAutoCacheOffline,
                                onToggleNotifications = viewModel::setNotificationsEnabled,
                                onOpenLiveWeb = { openLiveWeb(NEWS_URL) },
                                onOpenWhatsApp = { IntentUtils.openWhatsApp(context) },
                                onShareApp = {
                                    IntentUtils.shareArticle(
                                        context,
                                        "Aplikasi Berita Resmi Samudera Kepri",
                                        NEWS_URL
                                    )
                                }
                            )
                        }

                        composable(
                            route = ROUTE_ARTICLE,
                            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val articleId = backStackEntry.arguments?.getString("articleId").orEmpty()
                            val article by viewModel.getArticleFlow(articleId)
                                .collectAsStateWithLifecycle(initialValue = null)

                            ArticleDetailScreen(
                                article = article,
                                fontSizeScale = uiState.userPreferences.fontSizeScale,
                                hasGelarFaktaAccess = uiState.userPreferences.hasGelarFaktaAccess,
                                onBack = { navController.popBackStack() },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onShare = { item ->
                                    IntentUtils.shareArticle(context, item.title, item.articleUrl)
                                },
                                onOpenInWebView = openLiveWeb,
                                onOpenWhatsApp = { url -> IntentUtils.openWhatsApp(context, url) },
                                onUnlockExclusiveAccess = viewModel::grantGelarFaktaAccess
                            )
                        }

                        composable(
                            route = ROUTE_WEBVIEW,
                            arguments = listOf(
                                navArgument("url") {
                                    type = NavType.StringType
                                    defaultValue = NEWS_URL
                                }
                            )
                        ) { backStackEntry ->
                            val targetUrl = backStackEntry.arguments?.getString("url") ?: NEWS_URL
                            WebPortalScreen(
                                initialUrl = targetUrl,
                                isOnline = uiState.isOnline,
                                cachedArticlesCount = uiState.allArticles.size,
                                onClose = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
