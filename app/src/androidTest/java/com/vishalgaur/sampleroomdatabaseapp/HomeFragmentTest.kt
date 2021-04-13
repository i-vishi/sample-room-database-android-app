package com.vishalgaur.sampleroomdatabaseapp

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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
			it.sharedViewModel.status.getOrAwaitValue()
		}
		onView(withId(R.id.data_layout))
				.check(matches(withEffectiveVisibility(Visibility.GONE)))
		onView(withId(R.id.homeEmptyTextView)).check(matches(isDisplayed()))
		onView(withId(R.id.homeEmptyTextView)).check(matches(withText("Nothing to search!")))
	}

	@Test
	fun hasData_showLayout() {
		homeScenario.onFragment {
			val id = "1234"
			val name = "Vishal"
			val email = "   vishal@mail.com "
			val mob = "  7056897878"
			val dob = "11/11/1999"
			it.sharedViewModel.submitData(id, name, email, mob, dob)
			it.sharedViewModel.userData.getOrAwaitValue()
		}
		onView(withId(R.id.data_layout))
				.check(matches(withEffectiveVisibility(Visibility.GONE)))
		onView(withId(R.id.homeEmptyTextView)).check(matches(isDisplayed()))
		onView(withId(R.id.homeEmptyTextView)).check(matches(withText("Search to Get Information")))
	}

	@Test
	fun onFabClick_navigateToEditFragment() {
		onView(withId(R.id.fabAddEdit)).perform(click())
		assertEquals(R.id.editFragment, navController.currentDestination?.id)
	}

	@Test
	fun userCanEnterInSearch() {
		insertInSearchEditText("789")
	}

	@Test
	fun userCanClickSearch() {
		clickSearchButton()
	}

	@Test
	fun onSearch_validId_returnsData() {
		val id = "1234"
		val name = "Vishal"
		val email = "   vishal@mail.com "
		val mob = "  7056897878"
		val dob = "11/11/1999"
		homeScenario.onFragment {
			it.sharedViewModel.submitData(id, name, email, mob, dob)
			it.sharedViewModel.status.getOrAwaitValue()
		}
		insertInSearchEditText("1234")
		clickSearchButton()

		onView(withId(R.id.search_error_text_view)).check(matches(not(isDisplayed())))
		onView(withId(R.id.data_layout)).check(matches(isDisplayed()))
		onView(withId(R.id.homeEmptyTextView)).check(matches(not(isDisplayed())))
		onView(withId(R.id.detail_id)).check(matches(withText(id)))
	}

	@Test
	fun onSearch_invalidId_returnsError() {
		insertInSearchEditText("00045s")
		clickSearchButton()

		onView(withId(R.id.search_error_text_view))
				.check(matches(isDisplayed()))
				.check(matches(withText("Input Correct ID")))
		onView(withId(R.id.data_layout)).check(matches(not(isDisplayed())))
	}

	@Test
	fun onSearch_empty_returnsError() {
		insertInSearchEditText("")
		clickSearchButton()

		onView(withId(R.id.search_error_text_view))
				.check(matches(isDisplayed()))
				.check(matches(withText("ID can not be blank")))
		onView(withId(R.id.data_layout)).check(matches(not(isDisplayed())))
	}

	@Test
	fun onSearch_validAndNotExists_returnsError() {
		val id = "1234"
		val name = "Vishal"
		val email = "   vishal@mail.com "
		val mob = "  7056897878"
		val dob = "11/11/1999"
		homeScenario.onFragment {
			it.sharedViewModel.submitData(id, name, email, mob, dob)
			it.sharedViewModel.status.getOrAwaitValue()
		}
		insertInSearchEditText("14")
		clickSearchButton()

		onView(withId(R.id.search_error_text_view))
				.check(matches(isDisplayed()))
				.check(matches(withText("Not Found!")))
		onView(withId(R.id.data_layout)).check(matches(not(isDisplayed())))
	}

	private fun insertInSearchEditText(query: String) =
			onView(withId(R.id.search_box_edit_text)).perform(clearText(), typeText(query))

	private fun clickSearchButton() =
			onView(withId(R.id.search_btn)).perform(click())


}