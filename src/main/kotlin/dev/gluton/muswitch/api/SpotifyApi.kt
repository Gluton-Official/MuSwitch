package dev.gluton.muswitch.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.ktorfit
import dev.gluton.muswitch.dotenv
import dev.gluton.muswitch.httpClient
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.util.encodeBase64
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration.Companion.seconds

private val apiUrl: Url = URLBuilder(
    protocol = URLProtocol.HTTPS,
    host = "api.spotify.com",
    pathSegments = listOf("v1", "" /* trailing slash */),
).build()

private val credentialsUrl: Url = URLBuilder(
    protocol = URLProtocol.HTTPS,
    host = "accounts.spotify.com",
    pathSegments = listOf("api", "" /* trailing slash */),
).build()

val spotifyApi = ktorfit {
    httpClient(httpClient)
    baseUrl(apiUrl.toString())
}.create<SpotifyApi>()

val spotifyCredentialsApi = ktorfit {
    httpClient(httpClient)
    baseUrl(credentialsUrl.toString())
}.create<SpotifyCredentialsApi>()

private var authorizationToken = runBlocking { spotifyCredentialsApi.getToken() }
    get() {
        if (field.hasExpired) {
            field = runBlocking { spotifyCredentialsApi.getToken() }
        }
        return field
    }

interface SpotifyApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 1,
        @Header("Authorization") authorization: String = Authorization.bearer(authorizationToken.accessToken),
    ): SpotifySearchResponse

    @GET("tracks/{id}")
    suspend fun getTrack(
        @Path("id") id: String,
        @Header("Authorization") authorization: String = Authorization.bearer(authorizationToken.accessToken),
    ): SpotifyTrack?
}

interface SpotifyCredentialsApi {
    @POST("token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getToken(
        @Query("grant_type") grantType: String = "client_credentials",
        @Header("Authorization") authorization: String = Authorization.basic(dotenv["SPOTIFY_CLIENT_ID"], dotenv["SPOTIFY_CLIENT_SECRET"]),
    ): Token
}

@Serializable
data class Token(
    @SerialName("access_token")
    var accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    var expiresIn: Int,
) {
    private val expirationTime = Clock.System.now() + expiresIn.seconds
    val hasExpired: Boolean get() = Clock.System.now() >= expirationTime
}

@Serializable
data class SpotifySearchResponse(
    val tracks: SpotifyTrackSearch,
)

@Serializable
data class SpotifyTrackSearch(
    val items: List<SpotifyTrack>,
)

@Serializable
data class SpotifyTrack(
    @SerialName("id")
    val trackId: String,
    val name: String,
    val artists: List<SpotifyArtist>,
    @SerialName("external_urls")
    val externalUrls: ExternalUrls,
) {
    @Transient
    val url = Url(externalUrls.spotify)
}

@Serializable
data class SpotifyArtist(
    val name: String,
)

@Serializable
data class ExternalUrls(
    val spotify: String,
)

object Authorization {
    fun bearer(token: String) = "Bearer $token"
    fun basic(clientId: String, clientSecret: String) = "Basic ${"$clientId:$clientSecret".encodeBase64()}"
}
