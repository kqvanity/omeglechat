package com.chatter.omeglechat.videoscreen.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.videoscreen.components.VideoScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    // todo: Invalidate the permission later on, to further customize how things will be if the user clicked NOPE.
    var granted by remember { mutableStateOf("") }
    Snackbar {
        Text(granted)
    }

    val verticalPagerState = rememberPagerState { 10 }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            granted = "Granted"
        }
    VerticalPager(state = verticalPagerState) {
        VideoPage(
            pageIndex = it,
            miniCameraClickCallback = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }

}

@Preview
@Composable
fun VideoScreenPreview() {
    OmegleChatTheme {
        Surface (modifier = Modifier .fillMaxSize()) {
            VideoScreen(
                navController = rememberNavController()
            )
        }
    }
}