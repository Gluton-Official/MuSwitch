package dev.gluton.muswitch.platform

import dev.gluton.muswitch.api.SpotifyArtist
import dev.gluton.muswitch.api.spotifyApi
import io.ktor.http.Url

@Suppress("unused")
object Spotify : Platform("spotify.com") {
    override suspend fun search(track: Track): Url? =
        spotifyApi.search(track.simpleString).tracks.items.firstOrNull()?.url

    override suspend fun getTrack(url: Url): Track? {
        val pathSegments = url.pathSegments.filterNot { it.isBlank() }
        val trackId = pathSegments.takeIf { it.firstOrNull() == "track" }?.lastOrNull() ?: return null
        return spotifyApi.getTrack(trackId)?.let { track ->
            Track(track.name, track.artists.map(SpotifyArtist::name))
        }
    }
}
