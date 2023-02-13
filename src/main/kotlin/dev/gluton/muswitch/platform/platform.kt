package dev.gluton.muswitch.platform

import dev.gluton.muswitch.domain
import io.ktor.http.Url
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

val platforms = Platform::class.sealedSubclasses.map { it.objectInstance!! }

sealed class Platform(vararg domains: String) {
    val domains = domains.toSet()

    abstract suspend fun getTrack(url: Url): Track?
    abstract suspend fun search(track: Track): Url?

    fun searchAll(tracks: List<Track>): List<Url?> = runBlocking {
        tracks.map { track -> async { search(track) } }.awaitAll()
    }
}

data class Track(val title: String, val artists: List<String>) {
    private val whitespace = Regex("\\s+")
    private val grouping = Regex("[\\[({]((extended.*?)|(.*?release))[])}]", RegexOption.IGNORE_CASE)

    val simpleString: String by lazy {
        val simpleTitle = title.takeWhile { it != '|' } // remove anything after a pipe
            .replace(grouping, "") // remove any grouping that includes "extended" (e.g. [extended mix])
            // don't include special characters, then reduce any whitespace to a single space
            .filter { it.isLetterOrDigit() || it.isWhitespace() }.replace(whitespace, " ")
        // if the title has a dash, it probably already has the artist name
        val simpleArtist = artists.takeUnless { title.contains('-') }?.joinToString(" ")

        return@lazy simpleTitle + simpleArtist?.let { " $it" }.orEmpty()
    }

    companion object {
        fun from(url: Url): Track? = runBlocking {
            platforms.firstOrNull { platform -> url.domain in platform.domains }?.getTrack(url)
        }
    }
}
