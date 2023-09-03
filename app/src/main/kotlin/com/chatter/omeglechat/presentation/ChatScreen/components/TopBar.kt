package com.chatter.omeglechat.ChatScreen

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.R
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopChattingBar(
    scrollBehavior: TopAppBarScrollBehavior,
    connectionState: String,
    commonInterests: List<String>,
    searchButtonCallback: () -> Unit,
    arrowBackCallback: () -> Unit,
    modifier: Modifier = Modifier,
) {

    TopAppBar(
        title = {
            Column(
            ) {
                Text(
                    text = connectionState,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (commonInterests.isNotEmpty() && commonInterests.first().isNotEmpty()) "You both like ${commonInterests.joinToString(", ") }!" else "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.bodySmall,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = arrowBackCallback
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = searchButtonCallback
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(MaterialTheme.colorScheme.secondary),
        scrollBehavior = scrollBehavior,
        modifier = Modifier
            .clip(RoundedCornerShape(
                bottomStart = 5.dp,
                bottomEnd = 5.dp
            ))
    )
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, heightDp = 300)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, heightDp = 300)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewTopBar() {
//    OmegleChatTheme() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
           TopChattingBar(
               scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
               connectionState = stringResource(id = R.string.connected),
               commonInterests = listOf<String>("Love", "Train"),
               searchButtonCallback = {},
               arrowBackCallback = {}
           )
        }
//    }
}