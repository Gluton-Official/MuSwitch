package dev.gluton.muswitch.platform

import com.linkedin.urls.Url
import dev.gluton.muswitch.dotenv
import dev.gluton.muswitch.json
import dev.gluton.muswitch.TrackData
import dev.gluton.muswitch.util.httpGet
import dev.gluton.muswitch.util.tryOrNull
import dev.gluton.muswitch.util.urlWithoutQuery
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Suppress("unused")
object SoundCloud : Platform("soundcloud.com") {
    private val SEARCH_QUERY = "https://api-v2.soundcloud.com/search/tracks?client_id=${dotenv["SOUNDCLOUD_CLIENT_ID"]}&q="
    private val TRACK_QUERY = "https://api-v2.soundcloud.com/resolve?client_id=${dotenv["SOUNDCLOUD_CLIENT_ID"]}&url="

    override suspend fun extractTrackData(url: Url): TrackData? {
        return httpGet((TRACK_QUERY + url.urlWithoutQuery))?.let { response ->
            tryOrNull { json.decodeFromString<SoundCloudTrack>(response) }?.run {
                TrackData(publisherMetadata?.releaseTitle ?: title, listOf(publisherMetadata?.artist ?: user.username))
            }
        }
    }

    override suspend fun getTrackUrl(trackData: TrackData): Url? {
        return httpGet(SEARCH_QUERY, trackData.toSimpleString())?.let { response ->
            Json.parseToJsonElement(response).jsonObject["collection"]?.jsonArray?.firstOrNull()?.let { track ->
                Url.create(json.decodeFromJsonElement<SoundCloudTrack>(track).permalinkUrl)
            }
        }
    }

    @Serializable
    private data class SoundCloudTrack(
        @SerialName("permalink_url")
        val permalinkUrl: String,
        val title: String,
        val user: User,
        @SerialName("publisher_metadata")
        val publisherMetadata: PublisherMetadata? = null,
    ) {
        @Serializable
        data class User(val username: String)

        @Serializable
        data class PublisherMetadata(
            val artist: String? = null,
            @SerialName("release_title")
            val releaseTitle: String? = null,
        )
    }
}
