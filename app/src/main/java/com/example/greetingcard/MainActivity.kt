package com.example.greetingcard

import android.annotation.SuppressLint
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UsersScreen()
        }
    }
}

@Composable
fun UsersScreen() {
    val viewModel = viewModel<UsersViewModel>()
    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    Column {
        if (viewModel.loading) {
            Text(text = stringResource(R.string.loading_message))
        } else {
            UserList(users = viewModel.users)
        }
    }
}

@Composable
fun UserList(users: List<User>) {
    val selectedUser = remember { mutableStateOf<User?>(null) }
    LazyColumn {
        items(users) { user ->
            UserItem(
                user = user
            ) { selectedUser.value = user }
        }
    }

    selectedUser.value?.let { user ->
        UserDetailDialog(user = user) {
            selectedUser.value = null
        }
    }
}

@Composable
fun UserItem(user: com.example.greetingcard.User, onItemClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.user_name_text) + " ${user.name}",
        modifier = Modifier.clickable { onItemClick() }
    )
}
@Composable
fun UserDetailDialog(user: User, onDismiss: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(text = stringResource(R.string.user_details_text)) },
        text = {
            Column {
                Text(text = stringResource(R.string.user_email_text) + "${user.email}")
            }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(text = stringResource(R.string.close_text))
            }

        })
}
