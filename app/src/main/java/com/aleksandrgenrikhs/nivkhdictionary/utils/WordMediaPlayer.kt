package com.aleksandrgenrikhs.nivkhdictionary.utils

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri

object WordMediaPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun initPlayer(application: Application, url: String): MediaPlayer? {
        return try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(application, Uri.parse(url))
            }
            mediaPlayer
        } catch (e: Exception) {
            null
        }
    }

    fun destroyPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}