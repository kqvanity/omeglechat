package com.chatter.omeglechat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Quickreply
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsListMultiSelect
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// nap
private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        // Adding a migration based on the SharedPreferences name, since we're migrating from SharedPreferences.
        listOf(SharedPreferencesMigration(context, USER_PREFERENCES_NAME))
    }
)

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore( USER_PREFERENCES_NAME )
class PrefDataKeyValueStore(val context: Context) {

    private object PreferenceKeys {
        val interests: Preferences.Key<String> = stringPreferencesKey("interests")
    }

    suspend fun updateFlag(interests: String) = context.dataStore.edit { mutablePreferences ->
        mutablePreferences[PreferenceKeys.interests] = interests
    }

    fun watchFlag(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.interests] ?: ""
    }

}

data class UserPreferences(
    val notifications: Boolean,
    val darkMode: Boolean,
    val languageMatch: Boolean,
    val language: String,
    val interests: String,
    val autoReply: Boolean
)

//class UserPreferences {
//    private var bannedWords: MutableList<String> = mutableListOf(
//        "M"
//    )
//    private var mutualTopics: MutableList<String> = mutableListOf(
//        "gay"
//    )
//
//    fun getBannedWords(): MutableList<String> {
//        return (this.bannedWords)
//    }
//
//    fun getMutualTopics(): MutableList<String> {
//        return (this.mutualTopics)
//    }
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val currentLocalContext = LocalContext.current
    val prefDAtaKeyValueStore: PrefDataKeyValueStore = PrefDataKeyValueStore(context = currentLocalContext)
    val coroutineScope = rememberCoroutineScope()
    OmegleChatTheme () {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp)
        ) {
//            coroutineScope.launch {
//                val interests =
//                    PrefDataKeyValueStore(context = currentLocalContext).watchFlag()
//                        .stateIn(
//                            scope = coroutineScope,
//                            started = SharingStarted.Lazily,
//                            initialValue = ""
//                        )
//            }
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
                            text = "Application"
                        )
                    }
                ) {
                    SettingsCheckbox(
                        title = {
                            Text(
                                text = "Notifications"
                            )
                        },
                        subtitle = {
                           Text(
                               text = "Show notifications of incoming messages when the user is away.",
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
                        title = {
                            Text(
                                text = "Dark Mode",
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
                            text = "Profile"
                        )
                    }
                ) {
                    SettingsCheckbox(
                        enabled = true,
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
                                text = "Language Match",
                                style = Typography.bodyLarge
                            )
                        },
                        subtitle = {
                            Text(
                                text = "Attempt to match with others speaking the same language!",
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
                        title = {
                            Text(
                                text = "Language",
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
                                text = "Match with others speaking the same language.",
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
                                .padding(start = 16.dp, end = 16.dp)
                        )
                        // nap
//                        Spacer(modifier = Modifier.width(2.dp))
                        // nap
                        val interestsInitial = "Youtube, Omegle\n\t\t"    // Having a long trailing whitespace to mitigate for
                        val interests = PrefDataKeyValueStore(context = currentLocalContext).watchFlag().collectAsState( initial = "" ).value
//                        var interests by remember { mutableStateOf(interestsInitial) }
                        OutlinedTextField(
                            value = interests,
                            label = {
                                Text(
                                    text = "Interests",
                                )
                            },
                            onValueChange = { value ->
                                coroutineScope.launch {
                                    prefDAtaKeyValueStore.updateFlag(value)         // I guess this should be place elsewhere e.g., when the outlined text field loses focus
                                    Log.i("Setting", value)
                                }
                            },
                            maxLines = 1,
                            trailingIcon = {
                                // nap
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            prefDAtaKeyValueStore.updateFlag("")         // I guess this should be place elsewhere e.g., when the outlined text field loses focus
                                            Log.i("Setting", "Empty......")
                                        }
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
                                    if (it.isFocused && interests == interestsInitial) {
//                                        interests = ""
                                    } else if (!it.isFocused && (interests == interestsInitial || interests.isEmpty())){
//                                        interests = interestsInitial
                                        SolidColor(Color.Transparent)
                                    }
                                }
                        )
                    }
                    SettingsSlider(
                        title = {
                            Text(
                                text = "Age"
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
                }
                // nap
                Divider()
                SettingsGroup (
                    title = {
                        Text(
                            text = "Premium"
                        )
                    }
                ) {
                    SettingsSwitch(
                        title = {
                            Text(
                                text = "Auto reply",
                                style = Typography.bodyLarge
                            )
                        },
                        subtitle = {
                            Text(
                                text = "Attempt to reply to questions based on defined personal info e.g., name, age, country.",
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
                        - I'm thinking of appending another item for blocked people
                            - The blockage will based off the random ID generated by the other person.
                            - It should be reference by a timestamp rather than a random string.
                     */
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettings() {
    SettingsScreen(navController = null)
}
