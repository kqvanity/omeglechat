package com.chatter.omeglechat.ui.theme

import android.view.RoundedCorner
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(50.dp)
)

val more = RoundedCornerShape(
    bottomStart = 20.dp,
    topEnd = 20.dp
)
val message_incoming = RoundedCornerShape(
    topStart = 50.dp,
    topEnd = 0.dp,
    bottomEnd = 50.dp,
    bottomStart = 0.dp
)
val message_outgoing = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 50.dp,
    bottomEnd = 0.dp,
    bottomStart = 50.dp
)