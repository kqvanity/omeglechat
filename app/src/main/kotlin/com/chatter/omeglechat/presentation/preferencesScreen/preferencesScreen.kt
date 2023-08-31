package com.chatter.omeglechat

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Quickreply
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.chatter.omeglechat.preferences.PreferencesViewModel
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController?,
    preferencesViewModel: PreferencesViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentLocalContext = LocalContext.current
    OmegleChatTheme () {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
//                .padding(20.dp)
        ) {
            items (count = 1) {

//                AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ })
//                SettingsList(title = { /*TODO*/ }, items = )
//                SettingsMenuLink(
//                    title = { /*TODO*/ }
//                ) {
//                }
                // nap
                SettingsGroup(
                    title = {
                        Text(
                            text = stringResource(id = R.string.application)
                        )
                    }
                ) {
                    SettingsCheckbox(
                        enabled = false,
                        title = {
                            Text(
                                text = stringResource(id = R.string.notifications)
                            )
                        },
                        subtitle = {
                           Text(
                               text = stringResource(id = R.string.show_notifications),
                               style = Typography.bodySmall
                           )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null
                            )
                        }
                    )
                    SettingsSwitch(
                        enabled = false,
                        title = {
                            Text(
                                text = stringResource(id = R.string.dark_mode),
                                style = Typography.bodyLarge
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null
                            )
                        },
                        onCheckedChange = {
                            Toast.makeText(currentLocalContext, it.toString(), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                Divider()
                // nap
                SettingsGroup (
                    title = {
                        Text(
                            text = stringResource(id = R.string.profile)
                        )
                    }
                ) {
                    SettingsCheckbox(
                        enabled = false,
                        icon = {
                            /*
                                - nap
                             */
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(id = R.string.language_match),
                                style = Typography.bodyLarge
                            )
                        },
                        subtitle = {
                            Text(
                                text = stringResource(id = R.string.match_others),
                                style = Typography.bodySmall
                            )
                        },
                        onCheckedChange = { checkedNewValue ->
                            Toast.makeText(currentLocalContext, checkedNewValue.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                    // nap
                    SettingsList(
                        enabled = false,
                        title = {
                            Text(
                                text = stringResource(id = R.string.language),
                                style = Typography.bodyLarge
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null
                            )
                        },
                        subtitle = {
                            Text(
                                text = stringResource(id = R.string.match_others),
                                style = Typography.bodySmall
                            )
                        },
                        items = listOf(
                            "Arabic",
                            "Aymara"
                        )
                    )
                    // nap
    //                    SettingsListMultiSelect(
    //                        title = {
    //                            Text(
    //                                text = "Ok"
    //                            )
    //                        },
    //                        confirmButton = "Confirm",
    //                        items = listOf(
    //                            "three",
    //                            "four"
    //                        )
    //                    )
    //                    SettingsListDropdown(
    //                        title = {
    //                            Text(
    //                                text = "Gender"
    //                            )
    //                        },
    //                        items = listOf(
    //                            "five",
    //                            "six"
    //                        ),
    //                    )
                    SettingsSlider(
                        enabled = false,
                        title = {
                            Text(
                                text = stringResource(id = R.string.age)
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Timelapse,
                                contentDescription = null
                            )
                        },
                        valueRange = 0f..100f
                    )
                    // nap
//                    ListItem(headlineText = {
//                        Text(
//                            text = "Reply"
//                        )
//                    })
                    // nap
                    /*
                        - A custom row for a custom component in the Settings screen i.e., a custom entry that's not baked in the Compose-settings library.
                     */
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .defaultMinSize(minHeight = 72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Interests,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.padding_preferences_top),
                                    end = dimensionResource(id = R.dimen.padding_preferences_end)
                                )
                        )
//                        val userInterests = prefDataKeyValueStore.getUserInterests().collectAsState(initial = emptyList())
                        OutlinedTextField(
                            value = preferencesViewModel.userInterests.joinToString(", "),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.user_interests),
                                    style = Typography.bodyLarge,
                                    color = Color.LightGray
                                )
                            },
                            singleLine = true,
                            onValueChange = { newValue ->
                                preferencesViewModel.userInterests = newValue.split(", ").toMutableStateList()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        preferencesViewModel.userInterests = emptyList<String>().toMutableStateList()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircleOutline,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier
                                .onFocusChanged {
                                    if (it.isFocused) {
                                    } else {
//                                        SolidColor(Color.Transparent)
                                    }
                                }
                        )
                    }
                }
                // nap
                Divider()
                SettingsGroup (
                    title = {
                        Text(
                            text = stringResource(id = R.string.premium)
                        )
                    }
                ) {
                    SettingsSwitch(
                        enabled = false,
                        title = {
                            Text(
                                text = stringResource(id = R.string.auto_reply),
                                style = Typography.bodyLarge
                            )
                        },
                        subtitle = {
                            Text(
                                text = stringResource(id = R.string.auto_reply_description),
                                style = Typography.bodySmall
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Quickreply,
                                contentDescription = null
                            )
                        }
                    )
                    /*
                        - Blocking
                            - The blockage will based off the random ID generated by the other person.
                            - It should be reference by a timestamp rather than a random string.
                     */
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_miniscule))
                            .defaultMinSize(minHeight = 72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.padding_preferences_top),
                                    end = dimensionResource(id = R.dimen.padding_preferences_end)
                                )
                        )
                        var blockedWords by remember { mutableStateOf("") }
                        OutlinedTextField(
                            enabled = false,
                            value = blockedWords,
                            onValueChange = {
                                blockedWords = it
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.blocked_words),
                                    color = Color.LightGray
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircleOutline,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    SettingsSwitch(
                        enabled = false,
                        title = {
                            Text(
                                text = stringResource(id = R.string.auto_translate),
                                style = Typography.bodyLarge
                            )
                        },
                        subtitle = {
                            Text(
                                text = stringResource(id = R.string.auto_translate_description),
                                style = Typography.bodySmall
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettings() {
    SettingsScreen(
        navController = null
    )
}
