package com.aleksandrgenrikhs.nivkhdictionary.utils

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri

object WordMediaPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun initPlayer(application: Application, url: String, isUrlExist: Boolean): MediaPlayer? {
        return try {
            if (isUrlExist && mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(application, Uri.parse(url))
            }
            mediaPlayer
        } catch (e: Exception) {
            null
        }
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun destroyPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}