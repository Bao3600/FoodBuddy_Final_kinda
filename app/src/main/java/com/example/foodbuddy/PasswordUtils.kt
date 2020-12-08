package com.example.foodbuddy

import java.math.BigInteger
import java.security.MessageDigest

object PasswordUtils {

    fun salt(s1: String): String {
        return s1 + "beepboop"
    }

    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}