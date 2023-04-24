package io.github.garykam.autoadp.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.garykam.autoadp.R
import io.github.garykam.autoadp.utils.Utils

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(onSave: (String, String) -> Unit) {
    var username by remember { mutableStateOf(Utils.getUsername()) }
    var password by remember { mutableStateOf(Utils.getPassword()) }

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
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = username,
                    onValueChange = {
                        username = it
                    },
                    modifier = Modifier.padding(vertical = 5.dp),
                    label = { Text(text = stringResource(id = R.string.username)) })

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    modifier = Modifier.padding(vertical = 5.dp),
                    label = { Text(text = stringResource(id = R.string.password)) },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }
    )
}