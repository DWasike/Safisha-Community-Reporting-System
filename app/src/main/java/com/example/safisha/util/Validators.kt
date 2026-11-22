package com.example.safisha.util

object Validators {

    // A plain regex instead of Android's Patterns class, so it also works in unit tests
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidName(name: String): Boolean = name.trim().length >= 2

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun isValidLocation(location: String): Boolean = location.trim().length >= 3

    fun isValidDescription(description: String): Boolean = description.trim().length >= 10
}