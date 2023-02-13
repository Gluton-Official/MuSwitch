package dev.gluton.muswitch.platform

import dev.gluton.muswitch.api.youtubeApi
import dev.gluton.muswitch.domain
import io.ktor.http.Url

private const val DOMAIN = "youtube.com"
private const val SHORT_DOMAIN = "youtu.be"

@Suppress("unused")
object YouTube : Platform(DOMAIN, SHORT_DOMAIN) {
    override suspend fun search(track: Track): Url? =
        youtubeApi.search(track.simpleString).results.firstOrNull()?.resultId?.videoId?.url

    override suspend fun getTrack(url: Url): Track? {
        val videoId = when (url.domain) {
            DOMAIN -> url.parameters["v"]
            SHORT_DOMAIN -> url.pathSegments.lastOrNull()
            else -> null
        } ?: return null

        return youtubeApi.getVideos(videoId).videos.firstOrNull()?.snippet?.let { snippet ->
            Track(snippet.title, listOf(snippet.channelTitle))
        }
    }
}
