package dev.gluton.muswitch.platform

import com.linkedin.urls.Url
import dev.gluton.muswitch.dotenv
import dev.gluton.muswitch.json
import dev.gluton.muswitch.TrackData
import dev.gluton.muswitch.util.domain
import dev.gluton.muswitch.util.httpGet
import dev.gluton.muswitch.util.queryParameters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

private const val DOMAIN = "youtube.com"
private const val SHORT_DOMAIN = "youtu.be"

object YouTube : Platform(DOMAIN, SHORT_DOMAIN) {
    private const val VIDEO_PREFIX = "https://www.youtube.com/watch?v="

    private val SEARCH_QUERY = "https://youtube.googleapis.com/youtube/v3/search?key=${dotenv["YOUTUBE_API_KEY"]}&max_results=1&q="
    private val VIDEO_QUERY = "https://youtube.googleapis.com/youtube/v3/videos?key=${dotenv["YOUTUBE_API_KEY"]}&part=snippet&id="

    override suspend fun extractTrackData(url: Url): TrackData? {
        val videoId = when (url.domain) {
            DOMAIN -> url.queryParameters["v"]
            SHORT_DOMAIN -> url.path.dropWhile { it == '/' }
            else -> return null
        }
        return httpGet(VIDEO_QUERY + videoId)?.let { response ->
            Json.parseToJsonElement(response).jsonObject["items"]?.jsonArray?.firstOrNull()?.let { track ->
                val snippet = json.decodeFromJsonElement<YouTubeVideo>(track).snippet
                TrackData(snippet.title, listOf(snippet.channelTitle))
            }
        }
    }

    override suspend fun getTrackUrl(trackData: TrackData): Url? {
        return httpGet(SEARCH_QUERY, trackData.toSimpleString())?.let { response ->
            Json.parseToJsonElement(response).jsonObject["items"]?.jsonArray?.firstOrNull()?.let { track ->
                Url.create(VIDEO_PREFIX + json.decodeFromJsonElement<YouTubeSearchResult>(track).id.videoId)
            }
        }
    }

    @Serializable private data class YouTubeSearchResult(val id: ResultId) {
        @Serializable data class ResultId(val videoId: String)
    }

    @Serializable private data class YouTubeVideo(val id: String, val snippet: VideoSnippet) {
        @Serializable data class VideoSnippet(val title: String, val channelTitle: String)
    }
}
