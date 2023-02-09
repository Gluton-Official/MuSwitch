# μSwitch

Discord bot to find songs on different platforms.

Adds message context commands that find links to songs on supported platforms in the message, gets the song title and artist using the URLs, uses said title and artist to search on the selected platform, and sends back an ephemeral message containing links to the first search result of the songs.

## Supported platforms

- [Spotify](https://spotify.com)
- [SoundCloud](https://soundcloud.com)
- [YouTube](https://youtube.com)

## Setup

1. **Clone the repository**
2. **Give your Discord bot these scopes and permissions**:
   - `bot`
   - `applications.commands`
   - `Read Message History`
   - `Send Messages`
   - `Send Messages In Threads`
   - `Read Messages/View Channels`
   - `Use Application/Slash Commands`
3. **Define these environment variables** (you can alternatively create a `.env` file in the root directory with these values defined as well):
   - `BOT_TOKEN`: your [Discord bot](https://discord.com/developers/applications)'s token
   - `SPOTIFY_CLIENT_ID`: your [Spotify app](https://developer.spotify.com/dashboard/applications)'s client ID
   - `SPOTIFY_CLIENT_SECRET`: your [Spotify app](https://developer.spotify.com/dashboard/applications)'s client secret
   - `SOUNDCLOUD_CLIENT_ID`: open your browser's developer tools' network tab in SoundCloud, do some action on the webpage (e.g. click a button or refresh the page), and locate the `client_id` paramter in the payload tab of a packet
   - `YOUTUBE_API_KEY`: go to [YouTube's deveoper console](https://console.developers.google.com/), create a project, go to its Credentials page, create a Simple Key, and copy its API key
4. **Run with** `graldew run`
