package com.chatter.omeglechat.presentation.homescreeen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.R

@Composable
fun UserBubble(
    user: User,
    userClickCallback: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Box (
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiary)
            .size(50.dp)
            .clickable { userClickCallback(user) }
    ) {
        // TODO: The ability to show a green icon indicating whether the user is active or not.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
    }
}