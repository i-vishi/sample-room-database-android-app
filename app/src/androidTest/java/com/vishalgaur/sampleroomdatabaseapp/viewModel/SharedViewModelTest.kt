package com.vishalgaur.sampleroomdatabaseapp.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishalgaur.sampleroomdatabaseapp.DataStatus
import com.vishalgaur.sampleroomdatabaseapp.SearchErrors
import com.vishalgaur.sampleroomdatabaseapp.ViewErrors
import com.vishalgaur.sampleroomdatabaseapp.getOrAwaitValue
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedViewModelTest {
	private lateinit var sharedViewModel: SharedViewModel

	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setUp() {
		sharedViewModel = SharedViewModel(ApplicationProvider.getApplicationContext())
	}

	@Test
	fun submitData_noData_returnsEmptyError() {
		val id = ""
		val name = ""
		val email = ""
		val mob = ""
		val dob = ""

		sharedViewModel.submitData(id, name, email, mob, dob)
		assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_EMPTY))
	}

	@Test
	fun submitData_allValid_returnsNoError() {
		val id = "45"
		val name = "Vishal"
		val email = "   vishal@mail.com "
		val mob = "  7056897878"
		val dob = "01/01/1999"

		sharedViewModel.submitData(id, name, email, mob, dob)
		assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.NONE))
	}

	@Test
	fun submitData_invalidEmail_returnsEmailError() {
		val id = "26"
		val name = "Vishal"
		val email = "   vishal@mailom "
		val mob = "  7056897811"
		val dob = "01/01/1999"

		sharedViewModel.submitData(id, name, email, mob, dob)
		assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_EMAIL))
	}

	@Test
	fun submitData_invalidMobile_returnsMobileError() {
		val id = "78"
		val name = "Vishal"
		val email = "   vishal@mail.com "
		val mob = "  70568911 "
		val dob = "01/01/1999"

		sharedViewModel.submitData(id, name, email, mob, dob)
		assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_MOBILE))
	}

	@Test
	fun submitData_invalidEmailMobile_returnsEmailMobileError() {
		val id = "123"
		val name = "Vishal"
		val email = "   vishalailcom "
		val mob = "  70568911 "
		val dob = "01/01/1999"

		sharedViewModel.submitData(id, name, email, mob, dob)
		assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_EMAIL_MOBILE))
	}

	@Test
	fun searchData_null_returnsError() {
		val id = ""
		sharedViewModel.searchData(id)
		assertThat(sharedViewModel.searchErrStatus.getOrAwaitValue(), `is`(SearchErrors.ERR_EMPTY))
	}

	@Test
	fun searchData_invalid_returnsError() {
		val id = "00654jfv45"
		sharedViewModel.searchData(id)
		assertThat(sharedViewModel.searchErrStatus.getOrAwaitValue(), `is`(SearchErrors.ERR_INVALID))
	}

	@Test
	fun searchData_valid_returnsNoError() {
		val id = "123"
		sharedViewModel.searchData(id)
		assertThat(sharedViewModel.searchErrStatus.getOrAwaitValue(), `is`(isOneOf(SearchErrors.ERR_NOT_FOUND, SearchErrors.NONE)))
	}

	@Test
	fun setStatus_true_returnsLoaded() {
		sharedViewModel.setStatus(true)
		assertThat(sharedViewModel.status.getOrAwaitValue(), `is`(DataStatus.LOADED))
	}

	@Test
	fun setStatus_false_returnsEmpty() {
		sharedViewModel.setStatus(false)
		assertThat(sharedViewModel.status.getOrAwaitValue(), `is`(DataStatus.EMPTY))
	}
}