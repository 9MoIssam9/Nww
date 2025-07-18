package com.videa

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.extractors.helpers.*

class Videa : ExtractorApi() {
    override val name = "Videa"
    override val mainUrl = "https://videa.hu"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val doc = app.get(url).document
        val videoUrl = doc.select("video source").attr("src")
        return listOf(
            ExtractorLink(
                name,
                name,
                videoUrl,
                url,
                Qualities.Unknown.value
            )
        )
    }
}
