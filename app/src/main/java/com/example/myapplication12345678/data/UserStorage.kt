package com.example.myapplication12345678.data

import android.content.Context
import android.util.Log
import org.json.JSONObject

data class User(
    val firstName: String,
    val lastName: String,
    val login: String,
    val email: String,
    val password: String
)

object UserStorage {
    private const val PREFS_NAME = "pystart_user_prefs"
    private const val KEY_USER = "user_json"

    fun saveUser(context: Context, user: User) {
        val json = JSONObject().apply {
            put("firstName", user.firstName)
            put("lastName", user.lastName)
            put("login", user.login)
            put("email", user.email)
            put("password", user.password)
        }.toString()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER, json).apply()
        Log.d("UserStorage", "User saved: $json")
    }

    fun loadUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            val obj = JSONObject(json)
            User(
                firstName = obj.getString("firstName"),
                lastName = obj.getString("lastName"),
                login = obj.getString("login"),
                email = obj.getString("email"),
                password = obj.getString("password")
            )
        } catch (e: Exception) {
            Log.e("UserStorage", "Failed to parse user json", e)
            null
        }
    }
}
