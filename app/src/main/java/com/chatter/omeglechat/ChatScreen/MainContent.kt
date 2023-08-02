package com.chatter.omeglechat.ChatScreen

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.Message
import com.chatter.omeglechat.R
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainContent(
    paddingValue: PaddingValues,
    messages: MutableList<Message>,
    scrollState: LazyListState,         // nap
    modifier: Modifier = Modifier
) {
    LazyColumn(
//        contentPadding = PaddingValues(20.dp),
        contentPadding = paddingValue,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = scrollState,    // nap
//        reverseLayout = true,           // nap
        modifier = Modifier
//            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
    ) {
        items(
            messages
        ) { message ->
            // Setting different 'horizontalArrangement', and 'color' conditionally, depending on the type of the message.
            Row(
                horizontalArrangement = if (message.id == 0) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(id = R.dimen.padding_miniscule),
                        horizontal = dimensionResource(id = R.dimen.padding_small)
                    )
                    .fillMaxWidth()
            ) {
                Card(
                    onClick = { },
                    colors = CardDefaults
                        .cardColors(
                            containerColor = if (message.id == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        ),
                ) {
                    val localCurrentContext = LocalContext.current
                    var showMore by remember { mutableStateOf(false) }
                    var maxLines by remember { mutableStateOf(10) }
                    var moreLessButton by remember { mutableStateOf("more") }
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
                                // Don't forget to end 'pushStringAnnotation' with 'pop()'
                                pop()
//                                appendInlineContent(
//                                    id = "handle",
//                                )
                            } else {
                                append(it)
                            }
                            append(" ")
                        }
                    }
                    ClickableText(
                        /*
                            - nap
                                - Annotated string
                                    - It allows to style parts of our text or paragraph.
                                    - the withStyle method to apply the style to the portion of the text we need to style.
                         */
                        // text = "Message ${messages.value[it]}",
                        text = annotatedString,
//                        inlineContent = {
//                            "hello" to InlineTextContent(
//                                placeholder = Placeholder(
//                                    width
//                                )
//                            ) {
//                                TextButton(onClick = { /*TODO*/ }) {
//
//                                }
//                            }
//                        },
                        style = Typography.bodyMedium,
                        /*
                            - nap
                                - If text doesn't fix in the available space, we show an ellipsis and a button to view the entire text.
                                - onTextLayout
                                    - It returns text layer results with measurement information captured after the layout phase.
                                    - You don't emit UI inside onTextLayout lambda, because this call is not part of the composition.
                                        - Instead, you can change a state variable triggering a recomposition, and then decide to show a button or not based on the value of the state variable.
                         */
                        // Mitigate spammy messages
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            if (it.hasVisualOverflow) {
                                showMore = true
                            }
                        },
                        onClick = { offset ->
                            // The sole reason I'm having annotatedString as a separate variable, is because of its usage here (there's a better compact approach? whoknows!)
                            annotatedString.getStringAnnotations(tag = "handle", start = offset, end = offset).firstOrNull()?.let {
                                Toast.makeText(localCurrentContext, "${it.item} copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .wrapContentWidth()
                    )
                    if (showMore) {
                        TextButton(
                            onClick = {
                                maxLines = if (maxLines == 10) Int.MAX_VALUE else 10    // Not sure if this is the optimal way to go about it.
                                moreLessButton = if (moreLessButton == "more") "less" else "more"
                            },
                            content = {
                                Text(text = moreLessButton)
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                        )
                    }
                }
            }
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
            MainContent(
                paddingValue = PaddingValues(10.dp),
                messages = chatMessages,
                scrollState = rememberLazyListState()
            )
        }
    }
}