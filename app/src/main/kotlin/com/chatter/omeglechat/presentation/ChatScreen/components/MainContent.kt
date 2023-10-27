package com.chatter.omeglechat.ChatScreen

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatter.omeglechat.R
import com.chatter.omeglechat.domain.model.Message
import com.chatter.omeglechat.presentation.ChatScreen.ChatViewModelMock
import com.chatter.omeglechat.presentation.ChatScreen.components.MessageAlertDialog
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography

@Composable
internal fun MainContent(
    paddingValue: PaddingValues,
    messages: MutableList<Message>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = paddingValue,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = scrollState,
        reverseLayout = true,
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
    ) {
        items(
            messages
        ) { message ->
            // Setting different 'horizontalArrangement', and 'color' conditionally, depending on the type of the message.
            var messageAlertDialogVisible by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = if (message.id == 0) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                MessageItem(
                    message = message,
                    onMessageClick = {
                        messageAlertDialogVisible = true
                    }
                )
            }
            if (messageAlertDialogVisible) MessageAlertDialog(message = message)
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (message.id == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
            .wrapContentSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
            .clickable {
                onMessageClick()
            }
    ) {
        val localCurrentContext = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        val annotatedString = buildAnnotatedString {
            val handleStyle = SpanStyle(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.tertiary
            )
            val handlePattern = "@(\\w+)".toRegex()
            message.text.split(regex = "\\s+".toRegex()).forEach {
                if (it.matches(handlePattern)) {
                    pushStringAnnotation( tag = "handle", annotation = it )
                    withStyle(style = handleStyle) {
                        append(text = it)
                    }
                    pop()
                } else {
                    append(it)
                }
                append(" ")
            }
        }
        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_miniscule))
        ) {
            ClickableText(
                text = annotatedString,
                style = Typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 10,
                overflow = TextOverflow.Ellipsis,
                onClick = { offset ->
                    expanded = !expanded
                    annotatedString.getStringAnnotations(tag = "handle", start = offset, end = offset).firstOrNull()?.let {
                        Toast.makeText(localCurrentContext, "${it.item} copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .wrapContentWidth()
            )
            Text(
                text = message.date.dateTwelveFormat(),
                style = TextStyle(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMainContent() {
    OmegleChatTheme() {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val chatViewModel = ChatViewModelMock()
            MainContent(
                paddingValue = PaddingValues(10.dp),
                messages = chatViewModel.messages,
                scrollState = rememberLazyListState()
            )
        }
    }
}