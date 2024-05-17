package com.aleksandrgenrikhs.nivkhdictionary.utils

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WordMediaPlayer {

    private var mediaPlayer: MediaPlayer? = null

  suspend  fun initPlayer(application: Application, url: String): MediaPlayer? = withContext(Dispatchers.IO) {
      return@withContext try {
          if (mediaPlayer == null) {
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