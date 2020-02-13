package com.github.boybeak.main

data class Song(
    val id: Int, val data: String, val displayName: String, val size: Int, val mimeType: String,
    val dateAdded: Long, val dateModified: Long, val title: String, val titleKey: String,
    val duration: Int, val artistId: Int, val albumId: Int, val track: Int, val year: Int,
    val isRingtone: Int, val isMusic: Int, val isAlarm: Int, val isNotification: Int,
    val isPodcast: Int, val artistKey: String, val artist: String,
    val albumKey: String, val album: String
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Song) {
            return false
        }
        return data == other.data
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + data.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + size
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + dateModified.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + titleKey.hashCode()
        result = 31 * result + duration
        result = 31 * result + artistId
        result = 31 * result + albumId
        result = 31 * result + track
        result = 31 * result + year
        result = 31 * result + isRingtone
        result = 31 * result + isMusic
        result = 31 * result + isAlarm
        result = 31 * result + isNotification
        result = 31 * result + isPodcast
        result = 31 * result + artistKey.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumKey.hashCode()
        result = 31 * result + album.hashCode()
        return result
    }
}