package io.github.garykam.autoadp.ui.screens

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garykam.autoadp.R
import io.github.garykam.autoadp.ui.home.HomeViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    onSaveCredentials: () -> Unit,
    onScheduleClockOut: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
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
                            viewModel.saveCredentials()
                            onSaveCredentials()
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
                OutlinedTextField(value = viewModel.getUsername(),
                    onValueChange = { viewModel.setUsername(it) },
                    modifier = Modifier.padding(vertical = 5.dp),
                    label = { Text(text = stringResource(id = R.string.username)) })

                OutlinedTextField(
                    value = viewModel.getPassword(),
                    onValueChange = { viewModel.setPassword(it) },
                    modifier = Modifier.padding(vertical = 5.dp),
                    label = { Text(text = stringResource(id = R.string.password)) },
                    visualTransformation = PasswordVisualTransformation()
                )

                val timePickerDialog = TimePickerDialog(
                    LocalContext.current, { _, hourOfDay, minute ->
                        viewModel.saveTime("$hourOfDay:$minute")
                    }, viewModel.getHour(), viewModel.getMinute(), false
                )

                OutlinedButton(modifier = Modifier.padding(40.dp), onClick = {
                    timePickerDialog.show()
                }) {
                    Text(text = viewModel.getTime())
                }

                Button(onClick = onScheduleClockOut) {
                    Text(text = stringResource(id = R.string.schedule_clock_out))
                }
            }
        }
    )
}

