package dev.gluton.muswitch.platform

import com.linkedin.urls.Url
import com.linkedin.urls.detection.UrlDetector
import com.linkedin.urls.detection.UrlDetectorOptions
import dev.gluton.muswitch.TrackData
import dev.gluton.muswitch.util.domain
import kotlinx.coroutines.runBlocking

val platforms = Platform::class.sealedSubclasses.map { it.objectInstance!! }

fun getTracks(text: String): List<TrackData> = runBlocking {
    UrlDetector(text, UrlDetectorOptions.Default).detect().mapNotNull { url ->
        platforms.firstOrNull { platform -> url.domain in platform.domains }
            ?.also { println("Found ${it::class.simpleName} URL: $url") }
            ?.extractTrackData(url)
            ?.also { println("Extracted track data: $it") }
    }
}

sealed class Platform(vararg domains: String) {
    val domains = domains.toSet()

    abstract suspend fun extractTrackData(url: Url): TrackData?
    abstract suspend fun getTrackUrl(trackData: TrackData): Url?
}
