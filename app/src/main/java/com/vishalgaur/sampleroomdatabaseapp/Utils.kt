package com.vishalgaur.sampleroomdatabaseapp

import java.util.regex.Pattern

const val MOB_ERROR = "Enter valid mobile number!"
const val EMAIL_ERROR = "Enter valid email address!"


internal fun isEmailValid(email: String): Boolean {
    val EMAIL_PATTERN = Pattern.compile(
        "\\s*[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+\\s*"
    )
    return if (email.isEmpty()) {
        false
    } else {
        EMAIL_PATTERN.matcher(email).matches()
    }
}

internal fun isPhoneValid(phone: String): Boolean {
    val PHONE_PATTERN = Pattern.compile("^\\s*[6-9]\\d{9}\\s*\$")
    return if (phone.isEmpty()) {
        false
    } else {
        PHONE_PATTERN.matcher(phone).matches()
    }
}
//
//internal fun isDOBValid(dob: String): Boolean {
//
//}