package dev.gluton.muswitch

data class TrackData(val title: String, val artists: List<String>) {
    fun toSimpleString() = "$title ${artists.joinToString(" ")}"
    fun toHeaderString() = "${artists.joinToString(" & ")} - $title"
}
