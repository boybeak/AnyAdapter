package com.github.boybeak.main

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import org.json.JSONObject

class ScanTask(private val callback: (List<Song>) -> Unit) : AsyncTask<Context, Int, List<Song>>() {

    companion object {
        private val TAG = ScanTask::class.java.simpleName
    }

    override fun doInBackground(vararg params: Context?): List<Song> {
        if (params.isEmpty()) {
            return emptyList()
        }
        val context = params[0] ?: return emptyList()
        val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, null, null, null)
        cursor ?: return emptyList()
        val songs = ArrayList<Song>()

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            songs.add(getSong(cursor))
            cursor.moveToNext()
        }
        cursor.close()

        return songs
    }

    override fun onPostExecute(result: List<Song>?) {
        callback.invoke(result!!)
    }

    private fun getSong(cursor: Cursor): Song {
        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
        val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
        val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
        val size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
        val dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
        val dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))
        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
        val titleKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY))

        val duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
        val artistId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
        val albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
        val track = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))
        val year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR))
        val isRingtone = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE))
        val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC))
        val isAlarm = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_ALARM))
        val isNotification = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION))
        val isPodcast = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_PODCAST))
        val artistKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY))
        val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
        val albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY))
        val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))

        return Song(id, data, displayName, size, mimeType, dateAdded, dateModified, title, titleKey,
            duration, artistId, albumId, track, year, isRingtone, isMusic, isAlarm, isNotification,
            isPodcast, artistKey, artist, albumKey, album)
    }

}