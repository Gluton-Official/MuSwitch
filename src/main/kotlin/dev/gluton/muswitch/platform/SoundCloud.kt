package dev.gluton.muswitch.platform

import dev.gluton.muswitch.api.soundcloudApi
import io.ktor.http.Url

@Suppress("unused")
object SoundCloud : Platform("soundcloud.com") {
    override suspend fun search(track: Track): Url? =
        soundcloudApi.searchTracks(track.simpleString).tracks.firstOrNull()?.url

    override suspend fun getTrack(url: Url): Track =
         soundcloudApi.getTrack(url.toString()).let { track ->
             val title = track.publisherMetadata?.releaseTitle ?: track.title
             val artist = track.publisherMetadata?.artist ?: track.user.username
             Track(title, listOf(artist))
         }

}
