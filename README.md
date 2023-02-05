# μSwitch

Discord bot to find songs on different platforms.

Adds message context commands that find links to songs on supported platforms in the message, gets the song title and artist using the URLs, uses said title and artist to search on the selected platform, and sends back an ephemeral message containing links to the first search result of the songs.

## Supported platforms

- Spotify
- SoundCloud
- YouTube

## Setup

1. Clone the repository
2. Create a file called `.env` in the root directory
3. Define these fields in the `.env`:
```
BOT_TOKEN=
SPOTIFY_CLIENT_ID=
SPOTIFY_CLIENT_SECRET=
SOUNDCLOUD_CLIENT_ID=
YOUTUBE_API_KEY=
```
4. Give the Discord bot these permissions:
   - `Read Message History`
   - `Send Messages`
   - `Send Messages In Threads`
   - `Read Messages/View Channels`
   - `Use Application/Slash Commands`
4. Run `graldew run`
