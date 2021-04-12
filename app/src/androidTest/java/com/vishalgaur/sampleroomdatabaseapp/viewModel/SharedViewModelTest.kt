package com.vishalgaur.sampleroomdatabaseapp.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
    fun submitData_allValid_returnsNoError() {
        val name = "Vishal"
        val email = "   vishal@mail.com "
        val mob = "  7056897878"
        val dob = "01/01/1999"

        sharedViewModel.submitData(name, email, mob, dob)
        assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.NONE))
    }

    @Test
    fun submitData_invalidEmail_returnsEmailError() {
        val name = "Vishal"
        val email = "   vishal@mailom "
        val mob = "  7056897811"
        val dob = "01/01/1999"

        sharedViewModel.submitData(name, email, mob, dob)
        assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_EMAIL))
    }

    @Test
    fun submitData_invalidMobile_returnsMobileError() {
        val name = "Vishal"
        val email = "   vishal@mail.com "
        val mob = "  70568911 "
        val dob = "01/01/1999"

        sharedViewModel.submitData(name, email, mob, dob)
        assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_MOBILE))
    }

    @Test
    fun submitData_invalidEmailMobile_returnsEmailMobileError() {
        val name = "Vishal"
        val email = "   vishalailcom "
        val mob = "  70568911 "
        val dob = "01/01/1999"

        sharedViewModel.submitData(name, email, mob, dob)
        assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_EMAIL_MOBILE))
    }

    @Test
    fun submitData_noData_returnsEmptyError() {
        val name = ""
        val email = ""
        val mob = ""
        val dob = ""

        sharedViewModel.submitData(name, email, mob, dob)
        assertThat(sharedViewModel.errorStatus.getOrAwaitValue(), `is`(ViewErrors.ERR_EMPTY))
    }

}