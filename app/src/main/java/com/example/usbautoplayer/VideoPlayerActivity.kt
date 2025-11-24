package com.example.usbautoplayer
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

class VideoPlayerActivity : Activity() {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private val VIDEO_FILENAME = "stay_with_me.mov"
    private val PERMISSION_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerView = PlayerView(this)
        setContentView(playerView)
        if (!hasPerm()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST)
        } else initPlay()
    }
    private fun hasPerm() = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
    override fun onRequestPermissionsResult(r:Int, p:Array<out String>, g:IntArray){ if(g.isNotEmpty()&&g[0]==PackageManager.PERMISSION_GRANTED) initPlay() else finish() }

    private fun initPlay() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        val path = findUsb(VIDEO_FILENAME) ?: return
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(path)))
        player.setMediaItem(mediaItem)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.playWhenReady = true
    }
    private fun findUsb(f:String): String? {
        File("/storage").listFiles()?.forEach { d ->
            if(d.isDirectory){
                val c = File(d, f)
                if(c.exists()) return c.absolutePath
            }
        }
        return null
    }
    override fun onDestroy(){ super.onDestroy(); if(this::player.isInitialized) player.release() }
}