package com.vishalgaur.sampleroomdatabaseapp

import android.widget.DatePicker
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
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
		editScenario = launchFragmentInContainer(themeResId = R.style.Theme_SampleRoomDatabaseApp)
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
	fun userCanNotEnterDob() {
		onView(withId(R.id.input_dob_edit_text)).perform(scrollTo())
				.check(matches(not(isClickable())))
	}

	@Test
	fun userCanSelectDate() {
		insertInDobEditText(2002, 5, 21)
		onView(withId(R.id.input_dob_edit_text)).check(matches(withText("21/05/2002")))
	}

	@Test
	fun userCanEnterMobile() {
		insertInMobileEditText("8989895656")
	}

	@Test
	fun onSave_empty_showError() {
		insertInNameEditText("")
		insertInMobileEditText("")
		insertInEmailEditText("")
		clickSaveButton()

		onView(withId(R.id.input_save_btn)).check(matches(isDisplayed()))
	}

	@Test
	fun onSave_partiallyFilled_showError() {
		insertInMobileEditText("8989895656")
		insertInNameEditText("")
		insertInEmailEditText("vishal1236@somemail.com")
		clickSaveButton()

		onView(withId(R.id.input_error_text_view)).check(matches(isDisplayed()))
	}

	@Test
	fun onSave_completeAndValidForm_noErrorNavigateToHomeFragment() {
		insertInNameEditText("Vishal Gaur")
		insertInMobileEditText("8468778954")
		insertInEmailEditText("vishal123456@somemail.com")
		insertInDobEditText(2012, 7, 2)
		clickSaveButton()

		onView(withId(R.id.input_error_text_view))
				.check(matches(withEffectiveVisibility(Visibility.GONE)))
		assertEquals(navController.currentDestination?.id, R.id.homeFragment)
	}

	@Test
	fun onCancel() {
		clickCancelButton()
		assertEquals(navController.currentDestination?.id, R.id.homeFragment)
	}

	private fun insertInNameEditText(name: String) =
			onView(withId(R.id.input_name_edit_text)).perform(scrollTo(), clearText(), typeText(name))

	private fun insertInEmailEditText(email: String) =
			onView(withId(R.id.input_email_edit_text)).perform(scrollTo(), clearText(), typeText(email))

	private fun insertInMobileEditText(mob: String) =
			onView(withId(R.id.input_mobile_edit_text)).perform(scrollTo(), clearText(), typeText(mob))

	private fun insertInDobEditText(year: Int, month: Int, dayOfMonth: Int) {
		clickCalendarButton()
		onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
                PickerActions.setDate(year, month, dayOfMonth)
        )
		onView(withText("OK")).perform(click())
	}

	private fun clickCalendarButton() =
			onView(withId(R.id.input_select_date_btn)).perform(scrollTo(), click())

	private fun clickCancelButton() =
			onView(withId(R.id.input_cancel_btn)).perform(scrollTo(), click())

	private fun clickSaveButton() =
			onView(withId(R.id.input_save_btn)).perform(scrollTo(), click())
}