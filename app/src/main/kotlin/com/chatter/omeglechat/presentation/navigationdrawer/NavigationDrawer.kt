package com.chatter.omeglechat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatter.omeglechat.presentation.navigationdrawer.MenuItem
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

@Composable
fun WholeDrawer(
    onItemCallback: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(
                topEnd = 10.dp,
                bottomEnd = 10.dp
            ))
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = dimensionResource(id = R.dimen.padding_miniscule))
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                ),
                modifier = Modifier
                    .padding(
                        start = 10.dp,
                        bottom = 20.dp
                    )
            )
        }
        LazyColumn (
            modifier = Modifier
                .padding(
                    top = 10.dp
                )
        ) {
            items(MenuItem.entries.toList()) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
//                        .padding(dimensionResource(id = R.dimen.padding_medium))
                        .padding(horizontal = 10.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onItemCallback(item)
                        }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.title,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewNavigationDrawer() {
    OmegleChatTheme {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            WholeDrawer(
                onItemCallback = {}
            )
        }
    }
}