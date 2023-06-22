package com.chatter.omeglechat.ChatScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.Message
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
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
    ) {
        items(
            messages
        ) { message ->
            /*
                - Setting different 'horizontalArrangement', and 'color' conditionally, depending on the type of the message.
             */
            Row(
                horizontalArrangement = if (message.id == 0) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 20.dp
                    )
                    .fillMaxWidth()
            ) {
                Card(
                    onClick = { /*TODO*/ },
                    colors = CardDefaults.cardColors(containerColor = if (message.id == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    val localCurrentContext = LocalContext.current
                    var showMore by remember { mutableStateOf(false) }
                    var maxLines by remember { mutableStateOf(10) }
                    var moreLessButton by remember { mutableStateOf("more") }
                    val annotatedString = buildAnnotatedString {
                        val handleStyle = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        val handlePattern = "@(\\w+)".toRegex()
                        message.text.split(regex = "\\s+".toRegex()).forEach {
                            if (it.matches(handlePattern)) {
                                pushStringAnnotation( tag = "handle", annotation = it )
                                withStyle(style = handleStyle) {
                                    append(text = it)
                                }
                                pop()       // Don't forget to end 'pushStringAnnotation' with 'pop()'

//                                    appendInlineContent(
//                                        id = "handle",
//                                    )
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
                            .padding(10.dp)
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