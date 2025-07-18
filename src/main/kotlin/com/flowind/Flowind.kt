package com.flowind

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.Jsoup

class Flowind : MainAPI() {
    override var mainUrl = "https://flowind.net"
    override var name = "Flowind"
    override val hasMainPage = true
    override val hasQuickSearch = false
    override val supportedTypes = setOf(TvType.Anime)

    override suspend fun getMainPage(page: Int): HomePageResponse {
        val document = app.get(mainUrl).document
        val home = document.select(".poster .poster_img").mapNotNull {
            val href = it.parent()?.attr("href") ?: return@mapNotNull null
            val title = it.attr("alt")
            val posterUrl = it.attr("src")

            newAnimeSearchResponse(title, href, TvType.Anime) {
                this.posterUrl = posterUrl
            }
        }
        return HomePageResponse(listOf(HomePageList("أعمال أنمي", home)))
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h2.entry-title")?.text() ?: return null
        val poster = document.selectFirst(".poster img")?.attr("src")
        val episodes = document.select(".episodi a").map {
            val name = it.text()
            val epUrl = it.attr("href")
            Episode(epUrl, name)
        }
        return newAnimeLoadResponse(title, url, TvType.Anime) {
            this.posterUrl = poster
            this.episodes = episodes
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val document = app.get(data).document
        val iframeUrl = document.selectFirst("iframe")?.attr("src") ?: return
        loadExtractor(iframeUrl, data, subtitleCallback, callback)
    }
}
