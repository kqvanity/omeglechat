package com.chatter.omeglechat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(text = "Welcome to the Omegle app!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked,
            ) {
                Text(text = "Continue")
            }
        }
    }
}

//@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    OmegleChatTheme() {
        // I'm passing an empty callback here, because the preview is for demonstrational purposes
        OnboardingScreen(onContinueClicked = {})
    }
}