package com.chatter.omeglechat

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
//            OmegleChatTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    MainApp()
//                }
//            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_MASK)
@Composable
fun MainApp(
    modifier: Modifier = Modifier
) {
//    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

//    if (shouldShowOnboarding) {
//        OnboardingScreen()
//        {
//            shouldShowOnboarding = false
//        }
//    } else {
//        HomeScreen(
//            navController = null
//        )
//        ChatScreen()
//    }

}

@Composable
fun GreetingAndroid(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OmegleChatTheme {
        GreetingAndroid(name = "Android")
    }
}