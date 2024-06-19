package com.chatter.omeglechat.presentation.preferencesScreen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class DatastoreTest {

    private lateinit var preferencesScope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var stringKey: Preferences.Key<String>

    // TODO: I'm not sure If I should leave this one out here!
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var context: Context

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun createDatastore() {

        context = ApplicationProvider.getApplicationContext()

        // TODO: Should be abstracted away to its own abstract class with the default behavior!
        Dispatchers.setMain(UnconfinedTestDispatcher())

        preferencesScope = CoroutineScope(UnconfinedTestDispatcher())

        dataStore = PreferenceDataStoreFactory.create(scope = preferencesScope) {
            InstrumentationRegistry.getInstrumentation().targetContext.preferencesDataStoreFile(
                "settings_test"
            )
        }

        preferencesRepository = PreferencesRepository(context, preferencesScope)
    }

    @After
    fun removeDataStore() {
        File(
            ApplicationProvider.getApplicationContext<Context>().filesDir,
            "datastore"
        ).deleteRecursively()

        preferencesScope.cancel()
    }

    @Test
    fun just() = runTest {
        stringKey = stringPreferencesKey("age")
        val expectedPreferences = preferencesOf(stringKey to "20")
        assert(
            dataStore.edit {
                it[stringKey] = "20"
            } ==
            expectedPreferences
        )
        assert(
            expectedPreferences ==
            dataStore.data.first()
        )
    }

    @Test
    fun togglePrefTest() = runTest {
        assertEquals(
            preferencesOf(PreferencesRepository.AUTO_SKIP to true),
            preferencesRepository.togglePref(PreferencesRepository.AUTO_SKIP)
        )
    }

    @Test
    fun getDataTest() = runTest {
        assertEquals(
            LANGUAGES.ENGLISH,
            preferencesRepository.getPrefs(PreferencesRepository.USER_LANGUAGE, "").first()
        )
    }

    @Test
    fun getStateFlowTest() = runTest {
        preferencesRepository.getPrefsStateFlow(
            key = PreferencesRepository.USER_INTERESTS,
            initialValue = "",
            debounceLen = 100
        )
    }

}