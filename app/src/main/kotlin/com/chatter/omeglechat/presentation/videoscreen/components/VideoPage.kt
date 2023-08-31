package com.chatter.omeglechat.videoscreen.components

import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoPage(
    pageIndex: Int,
    miniCameraClickCallback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Text(text = pageIndex.toString())
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .size(120.dp)
                .align(Alignment.BottomStart)
                .clickable(enabled = true, onClick = {
                    miniCameraClickCallback()
                })
        ) {
            val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoCaptureIntent.resolveActivity(LocalContext.current.packageManager).also {
                videoCaptureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
            }
//            AndroidView(factory = {
//                VideoPlayer
//            })
            Text(text = pageIndex.toString())
        }
    }
}

