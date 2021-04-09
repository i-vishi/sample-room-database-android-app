package com.vishalgaur.sampleroomdatabaseapp

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class EditFragmentTest {
    private lateinit var editScenario: FragmentScenario<EditFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        editScenario =
            launchFragmentInContainer(themeResId = R.style.Theme_SampleRoomDatabaseApp)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
            editScenario.onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.vishalgaur.sampleroomdatabaseapp", appContext.packageName)
    }

    @Test
    fun userCanEnterName() {
        insertInNameEditText("Vishal Gaur  ")
    }

    @Test
    fun userCanEnterEmail() {
        insertInEmailEditText("vishal1236@somemail.com")
    }

    @Test
    fun userCanEnterMobile() {
        insertInMobileEditText("8989895656")
    }

    private fun insertInNameEditText(name: String) =
        onView(withId(R.id.input_name_edit_text))
            .perform(scrollTo(), typeText(name))

    private fun insertInEmailEditText(email: String) =
        onView(withId(R.id.input_email_edit_text))
            .perform(scrollTo(), typeText(email))

    private fun insertInMobileEditText(mob: String) =
        onView(withId(R.id.input_mobile_edit_text))
            .perform(scrollTo(), typeText(mob))
}