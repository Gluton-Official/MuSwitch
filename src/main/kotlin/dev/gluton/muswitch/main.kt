package dev.gluton.muswitch

import dev.gluton.muswitch.platform.Platform
import dev.gluton.muswitch.platform.Track
import dev.gluton.muswitch.platform.platforms
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.commands.CommandSetBuilder
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import mu.KotlinLogging

val logger = KotlinLogging.logger {}
val dotenv = dotenv()
val httpClient = HttpClient(CIO) {
    expectSuccess = true

    install(Logging)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

fun main() {
    @OptIn(KordPreview::class)
    bot(dotenv["BOT_TOKEN"]) {
        configure {
            defaultPermissions = Permissions(
                Permission.ReadMessageHistory,
                Permission.SendMessages,
                Permission.SendMessagesInThreads,
                Permission.ViewChannel,
                Permission.UseApplicationCommands,
            )
        }
    }
}

@Suppress("unused")
fun commands() = commands("μSwitch") {
    platforms.forEach(this::buildPlatformCommand)
}

private fun CommandSetBuilder.buildPlatformCommand(platform: Platform) {
    val platformName = platform::class.simpleName!!
    logger.debug { "Registering 'find-on-${platformName.lowercase()}' command" }
    message(
        displayText = "Find on $platformName",
        slashName = "find-on-${platformName.lowercase()}",
        description = "Find song in message on $platformName",
    ) {
        val message = arg.content
        val urls = findUrls(message)

        val tracks = runCatching { urls.mapNotNull(Track::from) }.getOrElse { exception ->
            respond("Failed getting track data from url${(exception.message ?: exception.cause)?.let { ": $it" } ?: ""}")
            return@message
        }
        val tracksWithUrl = runCatching { tracks.zip(platform.searchAll(tracks)) }.getOrElse { exception ->
            respond("Failed getting search results${(exception.message ?: exception.cause)?.let { ": $it" } ?: ""}")
            return@message
        }

        val responseMessage = tracksWithUrl.takeIf { it.isNotEmpty() }?.joinToString("\n") { (track, url) ->
            url?.toString() ?: "No track found on $platformName with artist(s) `${track.artists.joinToString()}` and title `${track.title}`"
        } ?: "No tracks found in message"

        respond(responseMessage)
    }
}
