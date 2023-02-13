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
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

private val videoUrl: URLBuilder get() = URLBuilder(
	protocol = URLProtocol.HTTPS,
	host = "www.youtube.com",
	pathSegments = listOf("watch"),
)

private val apiUrl: Url = URLBuilder(
	protocol = URLProtocol.HTTPS,
	host = "www.googleapis.com",
	pathSegments = listOf("youtube", "v3", "" /* trailing slash */),
).build()

val youtubeApi = ktorfit {
	httpClient(httpClient)
	baseUrl(apiUrl.toString())
}.create<YouTubeApi>()

interface YouTubeApi {
	@GET("search")
	suspend fun search(
		@Query("q") query: String,
		@Query("max_results") maxResults: Int = 1,
		@Query("key") key: String = dotenv["YOUTUBE_API_KEY"]!!,
	): YouTubeSearchResponse

	@GET("videos")
	suspend fun getVideos(
		@Query("id") videoId: String,
		@Query("part") part: String = "snippet",
		@Query("key") key: String = dotenv["YOUTUBE_API_KEY"]!!,
	): YouTubeVideoListResponse
}

@Serializable
data class YouTubeVideo(
	@SerialName("id")
	val videoId: VideoId,
	val snippet: VideoSnippet,
)

@Serializable
data class YouTubeVideoListResponse(
	@SerialName("items")
	val videos: List<YouTubeVideo>,
)

@Serializable
data class VideoSnippet(
	val title: String,
	val channelTitle: String,
)

@JvmInline
@Serializable
value class VideoId(val value: String) {
	val url: Url get() = videoUrl.apply {
		parameters.append("v", value)
	}.build()
}

@Serializable
data class YouTubeSearchResponse(
	@SerialName("items")
	val results: List<YouTubeSearchResult>
)

@Serializable
data class YouTubeSearchResult(@SerialName("id") val resultId: ResultId)

@Serializable
data class ResultId(val videoId: VideoId)
