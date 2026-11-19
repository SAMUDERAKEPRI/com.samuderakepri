package com.samuderakepri.data.remote

import android.text.Html
import android.util.Xml
import com.samuderakepri.data.local.ArticleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.max

data class MaritimeWeatherInfo(
    val areaName: String,
    val condition: String,
    val temperature: String,
    val windInfo: String,
    val sourceLabel: String,
    val isLiveFromBmkg: Boolean
)

class NewsRemoteDataSource {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    @Volatile
    private var wpCategoryIdCache: Map<String, Int> = emptyMap()

    suspend fun fetchLiveArticles(): Result<List<ArticleEntity>> = withContext(Dispatchers.IO) {
        val wpResult = runCatching { fetchFromWordPressApi(perPage = 30, categoryId = null) }
        if (wpResult.isSuccess && wpResult.getOrNull()?.isNotEmpty() == true) {
            return@withContext wpResult
        }

        val rssResult = runCatching { fetchFromRssFeed() }
        if (rssResult.isSuccess && rssResult.getOrNull()?.isNotEmpty() == true) {
            return@withContext rssResult
        }

        Result.failure(
            wpResult.exceptionOrNull()
                ?: rssResult.exceptionOrNull()
                ?: IllegalStateException("Gagal memuat berita dari samuderakepri.co.id")
        )
    }

    suspend fun fetchCategoryArticles(categoryName: String): Result<List<ArticleEntity>> =
        withContext(Dispatchers.IO) {
            if (categoryName == "Beranda" || categoryName == "Kabar Terkini") {
                return@withContext fetchLiveArticles()
            }

            runCatching {
                val categoryMap = ensureWpCategoriesLoaded()
                val matchedId = findMatchingWpCategoryId(categoryName, categoryMap)
                if (matchedId != null) {
                    fetchFromWordPressApi(perPage = 20, categoryId = matchedId, forcedCategory = categoryName)
                } else {
                    fetchFromWordPressApi(perPage = 30, categoryId = null)
                }
            }
        }

    private fun ensureWpCategoriesLoaded(): Map<String, Int> {
        if (wpCategoryIdCache.isNotEmpty()) return wpCategoryIdCache
        return try {
            val request = Request.Builder()
                .url("https://www.samuderakepri.co.id/wp-json/wp/v2/categories?per_page=50")
                .header("User-Agent", "SamuderaKepriAndroid/2.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyMap()
                val arr = JSONArray(response.body?.string().orEmpty())
                val map = mutableMapOf<String, Int>()
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val id = obj.optInt("id", -1)
                    val slug = obj.optString("slug").lowercase(Locale.ROOT)
                    val name = cleanHtml(obj.optString("name")).lowercase(Locale.ROOT)
                    if (id > 0) {
                        if (slug.isNotBlank()) map[slug] = id
                        if (name.isNotBlank()) map[name] = id
                    }
                }
                wpCategoryIdCache = map
                map
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun findMatchingWpCategoryId(categoryName: String, map: Map<String, Int>): Int? {
        if (map.isEmpty()) return null
        val keywords = when (categoryName) {
            "Investigasi" -> listOf("investigasi", "gelar-fakta", "gelar fakta")
            "Politik & Pemerintahan" -> listOf("politik-pemerintahan", "politik", "pemerintahan", "nasional")
            "Ekonomi & Bisnis" -> listOf("ekonomi-bisnis", "ekonomi", "bisnis")
            "Hukum & Kriminal" -> listOf("hukum-kriminal", "hukum", "kriminal")
            "Pendidikan" -> listOf("pendidikan", "edukasi")
            "Sosial & Masyarakat" -> listOf("sosial-masyarakat", "sosial", "masyarakat", "daerah")
            "Maritim" -> listOf("maritim", "kelautan", "perikanan")
            else -> emptyList()
        }
        for (kw in keywords) {
            map[kw]?.let { return it }
            map.entries.firstOrNull { (k, _) -> k.contains(kw) }?.value?.let { return it }
        }
        return null
    }

    private fun fetchFromWordPressApi(
        perPage: Int,
        categoryId: Int?,
        forcedCategory: String? = null
    ): List<ArticleEntity> {
        val url = buildString {
            append("https://www.samuderakepri.co.id/wp-json/wp/v2/posts?_embed&per_page=")
            append(perPage)
            if (categoryId != null) {
                append("&categories=").append(categoryId)
            }
        }

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "SamuderaKepriAndroid/2.0")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code}")
            }
            val body = response.body?.string().orEmpty()
            val array = JSONArray(body)
            val articles = mutableListOf<ArticleEntity>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = "wp-${obj.optLong("id", i.toLong())}"
                val title = cleanHtml(obj.optJSONObject("title")?.optString("rendered").orEmpty())
                val excerpt = cleanHtml(obj.optJSONObject("excerpt")?.optString("rendered").orEmpty())
                val rawContent = obj.optJSONObject("content")?.optString("rendered").orEmpty()
                val content = cleanHtmlParagraphs(rawContent).ifBlank { excerpt }
                val link = obj.optString("link", "https://www.samuderakepri.co.id")
                val dateStr = obj.optString("date_gmt", obj.optString("date", ""))
                val timestamp = parseIsoDate(dateStr)

                val embedded = obj.optJSONObject("_embedded")
                val authorName = embedded
                    ?.optJSONArray("author")
                    ?.optJSONObject(0)
                    ?.optString("name")
                    ?.takeIf { it.isNotBlank() }
                    ?: "Redaksi Samudera Kepri"

                val imageUrl = embedded
                    ?.optJSONArray("wp:featuredmedia")
                    ?.optJSONObject(0)
                    ?.optString("source_url")
                    ?.takeIf { it.isNotBlank() }
                    ?: extractFirstImageUrl(rawContent)

                val wpCategories = extractWpCategories(embedded)
                val normalizedCategory = forcedCategory ?: resolveBestCategory(wpCategories, "$title $excerpt")
                val isInvestigative = wpCategories.any {
                    it.contains("Investigasi", ignoreCase = true) ||
                        it.contains("Gelar Fakta", ignoreCase = true) ||
                        it.contains("Hukum", ignoreCase = true)
                } || normalizedCategory == "Investigasi" ||
                    normalizedCategory == "Hukum & Kriminal" ||
                    title.contains("Investigasi", ignoreCase = true) ||
                    title.contains("Gelar Fakta", ignoreCase = true) ||
                    title.contains("BPK", ignoreCase = true)

                val wordCount = content.split(Regex("\\s+")).size
                val readMinutes = max(2, (wordCount / 180) + 1)

                if (title.isNotBlank()) {
                    articles.add(
                        ArticleEntity(
                            id = id,
                            title = title,
                            excerpt = excerpt.ifBlank { content.take(150) },
                            content = content,
                            category = normalizedCategory,
                            region = detectKepriRegion("$title $content", wpCategories),
                            imageUrl = imageUrl,
                            articleUrl = link,
                            author = authorName,
                            publishedLabel = formatRelativeLabel(timestamp),
                            publishedTimestamp = timestamp,
                            readTimeMinutes = readMinutes,
                            isHero = categoryId == null && i == 0,
                            isInvestigative = isInvestigative,
                            isExclusive = false,
                            isCachedOffline = true,
                            cachedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
            return articles
        }
    }

    private fun fetchFromRssFeed(): List<ArticleEntity> {
        val request = Request.Builder()
            .url("https://www.samuderakepri.co.id/feed/")
            .header("User-Agent", "SamuderaKepriAndroid/2.0")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("RSS HTTP ${response.code}")
            }
            val xml = response.body?.string().orEmpty()
            return parseRssXml(xml)
        }
    }

    private fun parseRssXml(xml: String): List<ArticleEntity> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))

        val articles = mutableListOf<ArticleEntity>()
        var insideItem = false
        var title = ""
        var link = ""
        var description = ""
        var contentEncoded = ""
        var pubDate = ""
        var creator = ""
        val categories = mutableListOf<String>()
        var imageUrl = ""

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.name.lowercase(Locale.ROOT)
                    if (tag == "item") {
                        insideItem = true
                        title = ""
                        link = ""
                        description = ""
                        contentEncoded = ""
                        pubDate = ""
                        creator = ""
                        categories.clear()
                        imageUrl = ""
                    } else if (insideItem) {
                        when (tag) {
                            "title" -> title = parser.nextText().trim()
                            "link" -> link = parser.nextText().trim()
                            "description" -> description = parser.nextText().trim()
                            "content:encoded" -> contentEncoded = parser.nextText().trim()
                            "pubdate" -> pubDate = parser.nextText().trim()
                            "dc:creator" -> creator = parser.nextText().trim()
                            "category" -> {
                                val cat = cleanHtml(parser.nextText().trim())
                                if (cat.isNotBlank()) categories.add(cat)
                            }
                            "media:content", "enclosure" -> {
                                val urlAttr = parser.getAttributeValue(null, "url")
                                if (!urlAttr.isNullOrBlank() && imageUrl.isBlank()) {
                                    imageUrl = urlAttr
                                }
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("item", ignoreCase = true) && insideItem) {
                        insideItem = false
                        val cleanTitle = cleanHtml(title)
                        val cleanExcerpt = cleanHtml(description)
                        val fullBody = cleanHtmlParagraphs(contentEncoded.ifBlank { description })
                        val finalImage = imageUrl.ifBlank {
                            extractFirstImageUrl(contentEncoded.ifBlank { description })
                        }
                        val timestamp = parseRssDate(pubDate)
                        val normCat = resolveBestCategory(categories, "$cleanTitle $cleanExcerpt")
                        val isInv = normCat == "Investigasi" ||
                            normCat == "Hukum & Kriminal" ||
                            categories.any {
                                it.contains("Investigasi", ignoreCase = true) ||
                                    it.contains("Gelar Fakta", ignoreCase = true)
                            }

                        if (cleanTitle.isNotBlank()) {
                            articles.add(
                                ArticleEntity(
                                    id = "rss-${link.hashCode()}",
                                    title = cleanTitle,
                                    excerpt = cleanExcerpt.ifBlank { fullBody.take(150) },
                                    content = fullBody.ifBlank { cleanExcerpt },
                                    category = normCat,
                                    region = detectKepriRegion("$cleanTitle $fullBody", categories),
                                    imageUrl = finalImage,
                                    articleUrl = link.ifBlank { "https://www.samuderakepri.co.id" },
                                    author = creator.ifBlank { "Redaksi Samudera Kepri" },
                                    publishedLabel = formatRelativeLabel(timestamp),
                                    publishedTimestamp = timestamp,
                                    readTimeMinutes = max(2, fullBody.split(Regex("\\s+")).size / 180 + 1),
                                    isHero = articles.isEmpty(),
                                    isInvestigative = isInv,
                                    isExclusive = false,
                                    isCachedOffline = true,
                                    cachedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return articles
    }

    suspend fun fetchMaritimeWeather(): MaritimeWeatherInfo = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4=21.71.01.1001")
                .header("User-Agent", "SamuderaKepriAndroid/2.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string().orEmpty())
                    val dataArr = json.optJSONArray("data")
                    val firstData = dataArr?.optJSONObject(0)
                    val cuacaNested = firstData?.optJSONArray("cuaca")?.optJSONArray(0)
                    val current = cuacaNested?.optJSONObject(0)

                    if (current != null) {
                        val weatherDesc = current.optString("weather_desc", "Berawan")
                        val temp = current.optInt("t", 29)
                        val windSpeed = current.optDouble("ws", 12.0).toInt()
                        val windDir = current.optString("wd", "Barat Laut")

                        return@withContext MaritimeWeatherInfo(
                            areaName = "Perairan Batam, Bintan & Kepri",
                            condition = weatherDesc,
                            temperature = "${temp}°C",
                            windInfo = "Angin $windSpeed km/jam ($windDir) • Cek peringatan gelombang BMKG",
                            sourceLabel = "BMKG Terbuka (Live)",
                            isLiveFromBmkg = true
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }

        MaritimeWeatherInfo(
            areaName = "Perairan Kepri (Natuna, Anambas, Bintan, Batam)",
            condition = "Info Keselamatan Pelayaran",
            temperature = "28°–31°C",
            windInfo = "Pantau tinggi gelombang & arah angin terkini di portal resmi Maritim BMKG.",
            sourceLabel = "Panduan Maritim BMKG",
            isLiveFromBmkg = false
        )
    }

    private fun extractWpCategories(embedded: JSONObject?): List<String> {
        val result = mutableListOf<String>()
        val terms = embedded?.optJSONArray("wp:term") ?: return result
        for (i in 0 until terms.length()) {
            val inner = terms.optJSONArray(i) ?: continue
            for (j in 0 until inner.length()) {
                val term = inner.optJSONObject(j) ?: continue
                val taxonomy = term.optString("taxonomy")
                if (taxonomy == "category" || taxonomy == "post_tag") {
                    val name = cleanHtml(term.optString("name"))
                    if (name.isNotBlank() && !name.equals("Uncategorized", ignoreCase = true)) {
                        result.add(name)
                    }
                }
            }
        }
        return result
    }

    private fun resolveBestCategory(rawCategories: List<String>, fallbackText: String): String {
        val combined = rawCategories.joinToString(" ").lowercase(Locale.ROOT)
        return when {
            combined.contains("investigasi") || combined.contains("gelar fakta") -> "Investigasi"
            combined.contains("politik") || combined.contains("pemerintah") || combined.contains("nasional") -> "Politik & Pemerintahan"
            combined.contains("ekonomi") || combined.contains("bisnis") || combined.contains("umkm") -> "Ekonomi & Bisnis"
            combined.contains("hukum") || combined.contains("kriminal") || combined.contains("kejaksaan") || combined.contains("polri") -> "Hukum & Kriminal"
            combined.contains("pendidikan") || combined.contains("kampus") || combined.contains("sekolah") -> "Pendidikan"
            combined.contains("maritim") || combined.contains("kelautan") || combined.contains("nelayan") || combined.contains("pelayaran") -> "Maritim"
            combined.contains("sosial") || combined.contains("masyarakat") || combined.contains("desa") -> "Sosial & Masyarakat"
            else -> normalizeCategory(fallbackText)
        }
    }

    private fun normalizeCategory(raw: String): String {
        val lower = raw.lowercase(Locale.ROOT)
        return when {
            lower.contains("investigasi") || lower.contains("gelar fakta") -> "Investigasi"
            lower.contains("politik") || lower.contains("pemerintah") || lower.contains("nasional") || lower.contains("presiden") || lower.contains("dprd") -> "Politik & Pemerintahan"
            lower.contains("ekonomi") || lower.contains("bisnis") || lower.contains("investasi") -> "Ekonomi & Bisnis"
            lower.contains("hukum") || lower.contains("kriminal") || lower.contains("kasasi") || lower.contains("kejari") -> "Hukum & Kriminal"
            lower.contains("pendidikan") || lower.contains("generasi") || lower.contains("beasiswa") -> "Pendidikan"
            lower.contains("maritim") || lower.contains("kelautan") || lower.contains("nelayan") -> "Maritim"
            lower.contains("sosial") || lower.contains("masyarakat") -> "Sosial & Masyarakat"
            else -> "Kabar Terkini"
        }
    }

    private fun detectKepriRegion(text: String, categories: List<String> = emptyList()): String {
        val lower = (text + " " + categories.joinToString(" ")).lowercase(Locale.ROOT)
        return when {
            lower.contains("batam") -> "Batam"
            lower.contains("bintan") -> "Bintan"
            lower.contains("tanjungpinang") -> "Tanjungpinang"
            lower.contains("karimun") || lower.contains("kundur") || lower.contains("gemuruh") -> "Karimun"
            lower.contains("natuna") -> "Natuna"
            lower.contains("anambas") -> "Anambas"
            lower.contains("lingga") -> "Lingga"
            lower.contains("nasional") || lower.contains("presiden") -> "Nasional"
            else -> "Kepri"
        }
    }

    private fun extractFirstImageUrl(html: String): String {
        val regex = Regex("""<img[^>]+src=["']([^"']+)["']""", RegexOption.IGNORE_CASE)
        return regex.find(html)?.groupValues?.getOrNull(1).orEmpty()
    }

    private fun cleanHtml(html: String): String {
        if (html.isBlank()) return ""
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim()
    }

    private fun cleanHtmlParagraphs(html: String): String {
        if (html.isBlank()) return ""
        val spaced = html.replace(Regex("</p>|<br\\s*/?>", RegexOption.IGNORE_CASE), "\n\n")
        return Html.fromHtml(spaced, Html.FROM_HTML_MODE_LEGACY)
            .toString()
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    private fun parseIsoDate(dateStr: String): Long {
        if (dateStr.isBlank()) return System.currentTimeMillis()
        val formats = listOf("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val parsed = sdf.parse(dateStr.take(19))
                if (parsed != null) return parsed.time
            } catch (_: Exception) {
            }
        }
        return System.currentTimeMillis()
    }

    private fun parseRssDate(dateStr: String): Long {
        if (dateStr.isBlank()) return System.currentTimeMillis()
        return try {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun formatRelativeLabel(timestamp: Long): String {
        val diffMinutes = (System.currentTimeMillis() - timestamp) / 60_000L
        return when {
            diffMinutes < 1 -> "Baru saja"
            diffMinutes < 60 -> "$diffMinutes Menit Lalu"
            diffMinutes < 1440 -> "${diffMinutes / 60} Jam Lalu"
            diffMinutes < 10080 -> "${diffMinutes / 1440} Hari Lalu"
            else -> SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(timestamp)
        }
    }
}
