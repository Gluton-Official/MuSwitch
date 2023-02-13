package dev.gluton.muswitch.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.ktorfit
import dev.gluton.muswitch.dotenv
import dev.gluton.muswitch.httpClient
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

private val apiUrl: Url = URLBuilder(
    protocol = URLProtocol.HTTPS,
    host = "api-v2.soundcloud.com",
    pathSegments = listOf("" /* trailing slash */),
).build()

val soundcloudApi = ktorfit {
    httpClient(httpClient)
    baseUrl(apiUrl.toString())
}.create<SoundCloudApi>()

interface SoundCloudApi {
    @GET("resolve")
    suspend fun getTrack(
        @Query("url") url: String,
        @Query("client_id") clientId: String = dotenv["SOUNDCLOUD_CLIENT_ID"]!!,
    ): SoundCloudTrack

    @GET("search/tracks")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("client_id") clientId: String = dotenv["SOUNDCLOUD_CLIENT_ID"]!!,
    ): SoundCloudSearchResponse
}

@Serializable
data class SoundCloudTrack(
    @SerialName("permalink_url")
    val permalinkUrl: String,
    val title: String,
    val user: User,
    @SerialName("publisher_metadata")
    val publisherMetadata: PublisherMetadata? = null,
) {
    @Transient
    val url = Url(permalinkUrl)
}

@Serializable
data class User(val username: String)

@Serializable
data class PublisherMetadata(
    val artist: String? = null,
    @SerialName("release_title")
    val releaseTitle: String? = null,
)

@Serializable
data class SoundCloudSearchResponse(
    @SerialName("collection")
    val tracks: List<SoundCloudTrack>
)
