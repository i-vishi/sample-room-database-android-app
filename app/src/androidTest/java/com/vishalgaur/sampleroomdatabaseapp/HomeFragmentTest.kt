package com.vishalgaur.sampleroomdatabaseapp

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import com.vishalgaur.sampleroomdatabaseapp.database.UserDao
import com.vishalgaur.sampleroomdatabaseapp.database.UserDatabase
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {
    private lateinit var homeScenario: FragmentScenario<HomeFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        homeScenario = launchFragmentInContainer(themeResId = R.style.Theme_SampleRoomDatabaseApp)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
            homeScenario.onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }

    @Test
    fun noData_showNothing() {
        homeScenario.onFragment {
            it.sharedViewModel.clearData()
        }
        onView(withId(R.id.homeConstraintLayout))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.homeEmptyTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun hasData_showLayout() {
        homeScenario.onFragment {
            val name = "Vishal"
            val email = "   vishal@mail.com "
            val mob = "  7056897878"
            val dob = "11/11/1999"
            it.sharedViewModel.submitData(name, email, mob, dob)
        }
        onView(withId(R.id.homeEmptyTextView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.homeConstraintLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun onFabClick_navigateToEditFragment() {
        homeScenario.onFragment {
            it.sharedViewModel.clearData()
        }

        onView(withId(R.id.fabAddEdit)).perform(click())

        assertEquals(R.id.EditFragment, navController.currentDestination?.id)
    }
}