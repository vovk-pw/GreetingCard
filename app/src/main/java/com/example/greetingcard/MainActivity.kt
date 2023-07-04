package com.example.greetingcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greetingcard.ui.theme.GreetingCardTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonString = getJsonStringFromAssets("Users.json")
        val gson = Gson()
        val userListType = object : TypeToken<List<User>>() {}.type
        val users: List<User> = gson.fromJson(jsonString, userListType)

        setContent {
            GreetingCard {
                UserList(users)
            }
        }
    }

    data class User(val name: String, val last_name: String, val birth_date: String)

    @Throws(IOException::class)
    private fun getJsonStringFromAssets(fileName: String): String {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

    @Composable
    fun GreetingCard(content: @Composable () -> Unit) {
        GreetingCardTheme {
            Surface(
                //modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    }

    @Composable
    fun UserList(users: List<User>) {
        val selectedUser = remember { mutableStateOf<User?>(null) }

        LazyColumn {
            items(users) { user ->
                UserItem(
                    user = user,
                    onItemClick = { selectedUser.value = user }
                )
            }
        }

        selectedUser.value?.let { user ->
            UserDetailDialog(user = user) {
                selectedUser.value = null
            }
        }
    }

    @Composable
    fun UserItem(user: User, onItemClick: () -> Unit) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Name: ${user.name}, Last Name: ${user.last_name}",
            modifier = Modifier.clickable { onItemClick() }
        )
    }

    @Composable
    fun UserDetailDialog(user: User, onDismiss: () -> Unit) {
        val showDialog = remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "User Details") },
            text = {
                Column {
                    Text(text = "Name: ${user.name}")
                    Text(text = "Last Name: ${user.last_name}")
                    Text(text = "Birth Date: ${user.birth_date}")
                }
            },
            confirmButton = {
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text(text = "Close")
                }

            })
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        val sampleUsers = listOf(
            User("Alice", "Johnson", "1995-07-03")
        )
        GreetingCard {
            UserList(sampleUsers)
        }
    }
}