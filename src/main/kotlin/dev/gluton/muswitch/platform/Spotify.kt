package dev.gluton.muswitch.platform

import com.adamratzman.spotify.models.SimpleArtist
import com.adamratzman.spotify.spotifyAppApi
import com.linkedin.urls.Url
import dev.gluton.muswitch.TrackData
import dev.gluton.muswitch.dotenv
import dev.gluton.muswitch.util.paths
import kotlinx.coroutines.runBlocking

@Suppress("unused")
object Spotify : Platform("spotify.com") {
    private val api = runBlocking {
        spotifyAppApi(
            clientId = dotenv["SPOTIFY_CLIENT_ID"]!!,
            clientSecret = dotenv["SPOTIFY_CLIENT_SECRET"]!!,
        ).build()
    }

    override suspend fun extractTrackData(url: Url): TrackData? {
        return url.paths.takeIf { it.first() == "track" }?.let { (_, trackId) ->
            api.tracks.getTrack(trackId)?.let { track ->
                TrackData(track.name, track.artists.map(SimpleArtist::name))
            }
        }
    }

    override suspend fun getTrackUrl(trackData: TrackData): Url? {
        return api.search.searchTrack(trackData.toSimpleString()).first()?.let { firstTrack ->
            Url.create(firstTrack.externalUrls.spotify)
        }
    }
}
