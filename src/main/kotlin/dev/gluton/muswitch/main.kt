package dev.gluton.muswitch

import dev.gluton.muswitch.platform.getTracks
import dev.gluton.muswitch.platform.platforms
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import me.jakejmattson.discordkt.dsl.bot
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.commands.commands

val dotenv = dotenv()

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
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
    for (platform in platforms) {
        val platformName = platform::class.simpleName!!
        message(
            displayText = "Find on $platformName",
            slashName = "find-on-${platformName.lowercase()}",
            description = "Find song in message on $platformName",
        ) {
            val tracks = getTracks(arg.content).takeIf { it.isNotEmpty() }
            val response = tracks?.joinToString("\n") { trackData ->
                runBlocking {
                    platform.getTrackUrl(trackData)?.fullUrl?.also { println("Converted to $platformName URL: $it") } ?: "No track found on $platformName with name `${trackData.toHeaderString()}`"
                }
            } ?: "No valid track URLs found in message"
            respond(response)
        }
    }
}
