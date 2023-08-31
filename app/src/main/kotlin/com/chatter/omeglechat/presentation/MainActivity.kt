package com.chatter.omeglechat

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setSoftInputMode(        // Stop the keyboard pushing the top app bar off the screen.
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
//        )
//        val darkThemeState = chatViewModel.darkThemeState.value ?: false
        setContent {
            OmegleChatTheme(
                darkTheme = false
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Navigation()
                }
            }
        }
    }
}