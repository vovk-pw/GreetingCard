package com.example.greetingcard

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val address: Address,
    val phone: String,
    val website: String,
    val company: Company
)

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo
)

data class Geo(
    val lat: String,
    val lng: String
)

data class Company(
    val name: String,
    val catchPhrase: String,
    val bs: String
)

class UsersViewModel : ViewModel() {
    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    private val _loading = mutableStateOf(false)
    val loading: Boolean get() = _loading.value

    fun fetchUsers() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val fetchedUsers = fetchUsersFromApi()
                _users.addAll(fetchedUsers)
            } catch (e: Exception) {
                println("Error occurred: ${e.message}")
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun fetchUsersFromApi(): List<User> {
            val apiURL = "https://jsonplaceholder.typicode.com/users"
            return withContext(Dispatchers.IO) {
                val response = URL(apiURL).readText()
                val gson = Gson()
                gson.fromJson(response, Array<User>::class.java).toList()
            }
        }
    }