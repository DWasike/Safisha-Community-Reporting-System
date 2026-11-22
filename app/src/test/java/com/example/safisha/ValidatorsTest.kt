package com.example.safisha

import com.example.safisha.util.Validators
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun validEmail_isAccepted() {
        assertTrue(Validators.isValidEmail("jane@example.com"))
        assertTrue(Validators.isValidEmail("  jane.doe+tag@mail.co.ke "))
    }

    @Test
    fun invalidEmail_isRejected() {
        assertFalse(Validators.isValidEmail(""))
        assertFalse(Validators.isValidEmail("jane"))
        assertFalse(Validators.isValidEmail("jane@"))
        assertFalse(Validators.isValidEmail("jane@example"))
    }

    @Test
    fun password_needsSixCharacters() {
        assertFalse(Validators.isValidPassword("12345"))
        assertTrue(Validators.isValidPassword("123456"))
    }

    @Test
    fun name_needsTwoCharacters() {
        assertFalse(Validators.isValidName(" a "))
        assertTrue(Validators.isValidName("Jo"))
    }

    @Test
    fun location_needsThreeCharacters() {
        assertFalse(Validators.isValidLocation("ab"))
        assertTrue(Validators.isValidLocation("Ngong Road"))
    }

    @Test
    fun description_needsTenCharacters() {
        assertFalse(Validators.isValidDescription("too short"))
        assertTrue(Validators.isValidDescription("Large pothole near the junction"))
        assertFalse(Validators.isValidDescription("          "))
    }
}