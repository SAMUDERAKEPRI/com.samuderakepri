package com.samuderakepri.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samuderakepri.data.local.AppDatabase
import com.samuderakepri.data.local.ArticleEntity
import com.samuderakepri.data.local.UserPreferences
import com.samuderakepri.data.local.UserPreferencesRepository
import com.samuderakepri.data.remote.MaritimeWeatherInfo
import com.samuderakepri.data.remote.NewsRemoteDataSource
import com.samuderakepri.data.repository.NetworkMonitor
import com.samuderakepri.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val allArticles: List<ArticleEntity> = emptyList(),
    val filteredArticles: List<ArticleEntity> = emptyList(),
    val heroArticle: ArticleEntity? = null,
    val breakingArticle: ArticleEntity? = null,
    val investigativeArticles: List<ArticleEntity> = emptyList(),
    val bookmarkedArticles: List<ArticleEntity> = emptyList(),
    val selectedCategory: String = "Beranda",
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOnline: Boolean = true,
    val statusBannerMessage: String? = null,
    val maritimeWeather: MaritimeWeatherInfo = MaritimeWeatherInfo(
        areaName = "Perairan Kepri (Natuna, Anambas, Bintan, Batam)",
        condition = "Memuat info BMKG...",
        temperature = "29°C",
        windInfo = "Memeriksa prakiraan cuaca maritim terbuka BMKG...",
        sourceLabel = "BMKG Maritim",
        isLiveFromBmkg = false
    ),
    val userPreferences: UserPreferences = UserPreferences()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val remoteDataSource = NewsRemoteDataSource()
    private val repository = NewsRepository(database.articleDao(), remoteDataSource)
    private val preferencesRepository = UserPreferencesRepository(application)
    private val networkMonitor = NetworkMonitor(application)

    private val _selectedCategory = MutableStateFlow("Beranda")
    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    private val _maritimeWeather = MutableStateFlow(
        MaritimeWeatherInfo(
            areaName = "Perairan Kepri (Natuna, Anambas, Bintan, Batam)",
            condition = "Siaga Pelayaran",
            temperature = "29°C",
            windInfo = "Pantau gelombang & angin perairan Kepulauan Riau secara berkala.",
            sourceLabel = "BMKG Maritim",
            isLiveFromBmkg = false
        )
    )

    val categories = listOf(
        "Beranda",
        "Investigasi",
        "Kabar Terkini",
        "Politik & Pemerintahan",
        "Ekonomi & Bisnis",
        "Hukum & Kriminal",
        "Pendidikan",
        "Sosial & Masyarakat",
        "Maritim"
    )

    private val filterFlow = combine(
        _selectedCategory,
        _searchQuery,
        _isSearchActive,
        _isRefreshing,
        _statusBannerMessage
    ) { category, query, searchActive, refreshing, banner ->
        FilterParams(category, query, searchActive, refreshing, banner)
    }

    val uiState: StateFlow<MainUiState> = combine(
        repository.allArticles,
        repository.investigativeArticles,
        repository.bookmarkedArticles,
        preferencesRepository.preferencesFlow,
        networkMonitor.isOnline,
        filterFlow,
        _maritimeWeather
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val all = values[0] as List<ArticleEntity>
        @Suppress("UNCHECKED_CAST")
        val investigative = values[1] as List<ArticleEntity>
        @Suppress("UNCHECKED_CAST")
        val bookmarked = values[2] as List<ArticleEntity>
        val prefs = values[3] as UserPreferences
        val online = values[4] as Boolean
        val filter = values[5] as FilterParams
        val weather = values[6] as MaritimeWeatherInfo

        val queryTrim = filter.query.trim()
        val isAllFeed = filter.category == "Beranda" || filter.category == "Kabar Terkini"

        val matching = all.filter { article ->
            val matchesCategory = isAllFeed ||
                article.category.equals(filter.category, ignoreCase = true) ||
                (filter.category == "Investigasi" && article.isInvestigative)
            val matchesSearch = queryTrim.isBlank() ||
                article.title.contains(queryTrim, ignoreCase = true) ||
                article.excerpt.contains(queryTrim, ignoreCase = true) ||
                article.region.contains(queryTrim, ignoreCase = true) ||
                article.category.contains(queryTrim, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        val breaking = all.firstOrNull { it.isHero } ?: all.firstOrNull()
        val hero = if (filter.category == "Beranda" && queryTrim.isBlank()) {
            matching.firstOrNull { it.isHero } ?: matching.firstOrNull()
        } else {
            null
        }

        val feed = if (hero != null) {
            matching.filterNot { it.id == hero.id }
        } else {
            matching
        }

        MainUiState(
            allArticles = all,
            filteredArticles = feed,
            heroArticle = hero,
            breakingArticle = breaking,
            investigativeArticles = investigative,
            bookmarkedArticles = bookmarked,
            selectedCategory = filter.category,
            searchQuery = filter.query,
            isSearchActive = filter.searchActive,
            isRefreshing = filter.refreshing,
            isOnline = online,
            statusBannerMessage = filter.banner,
            maritimeWeather = weather,
            userPreferences = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialCache()
            if (networkMonitor.checkCurrentConnectivity()) {
                refreshNews(silent = true)
            }
            refreshMaritimeWeather()
        }
    }

    fun getArticleFlow(articleId: String): Flow<ArticleEntity?> =
        repository.getArticleById(articleId)

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        if (networkMonitor.checkCurrentConnectivity() && category != "Beranda") {
            viewModelScope.launch {
                repository.syncCategoryArticles(category)
            }
        }
    }

    fun toggleSearch() {
        val next = !_isSearchActive.value
        _isSearchActive.value = next
        if (!next) {
            _searchQuery.value = ""
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshNews(silent: Boolean = false) {
        if (!networkMonitor.checkCurrentConnectivity()) {
            if (!silent) {
                _statusBannerMessage.value =
                    "Perangkat offline. Menampilkan ${uiState.value.allArticles.size} berita tersimpan di cache lokal."
            }
            return
        }

        viewModelScope.launch {
            _isRefreshing.value = true
            val currentCategory = _selectedCategory.value
            val result = if (currentCategory == "Beranda" || currentCategory == "Kabar Terkini") {
                repository.syncLatestArticles()
            } else {
                repository.syncCategoryArticles(currentCategory)
            }
            refreshMaritimeWeather()
            _isRefreshing.value = false

            if (!silent) {
                _statusBannerMessage.value = result.fold(
                    onSuccess = { count -> "Berhasil menyinkronkan $count berita terbaru untuk mode offline." },
                    onFailure = { "Menampilkan berita cache lokal. Portal samuderakepri.co.id belum merespons." }
                )
            }
        }
    }

    private suspend fun refreshMaritimeWeather() {
        _maritimeWeather.value = repository.fetchMaritimeWeather()
    }

    fun clearStatusMessage() {
        _statusBannerMessage.value = null
    }

    fun toggleBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(article.id, article.isBookmarked)
            _statusBannerMessage.value = if (!article.isBookmarked) {
                "Artikel disimpan ke daftar bacaan offline."
            } else {
                "Artikel dihapus dari daftar tersimpan."
            }
        }
    }

    fun clearNonBookmarkedCache() {
        viewModelScope.launch {
            repository.clearNonBookmarkedCache()
            _statusBannerMessage.value = "Cache sementara dibersihkan. Artikel tersimpan tetap aman."
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setDarkTheme(enabled) }
    }

    fun setFontSizeScale(scale: Float) {
        viewModelScope.launch { preferencesRepository.setFontSizeScale(scale) }
    }

    fun setAutoCacheOffline(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setAutoCacheOffline(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setNotificationsEnabled(enabled) }
    }

    fun grantGelarFaktaAccess() {
        viewModelScope.launch {
            preferencesRepository.setGelarFaktaAccess(true)
            _statusBannerMessage.value = "Akses Penuh Laporan Gelar Fakta aktif!"
        }
    }

    override fun onCleared() {
        networkMonitor.unregister()
        super.onCleared()
    }

    private data class FilterParams(
        val category: String,
        val query: String,
        val searchActive: Boolean,
        val refreshing: Boolean,
        val banner: String?
    )
}
