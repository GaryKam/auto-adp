package io.github.garykam.clocker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun MainScreen(onClockOut: () -> Unit, onSave: (String, String) -> Unit, scheduleClockOut: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                    }

                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(onClick = {
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                onSave(username, password)
                            }
                            showMenu = false
                        }) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    }
                })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = username, onValueChange = {
                    username = it
                }, label = { Text(text = stringResource(id = R.string.username)) })

                OutlinedTextField(value = password, onValueChange = {
                    password = it
                }, label = { Text(text = stringResource(id = R.string.password)) })

                Button(onClick = onClockOut) {
                    Text(text = stringResource(id = R.string.clock_out))
                }

                Button(onClick = scheduleClockOut) {
                    Text(text = "Schedule Clock Out")
                }
            }
        }
    )
}