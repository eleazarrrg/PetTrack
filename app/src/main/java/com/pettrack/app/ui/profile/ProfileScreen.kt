package com.pettrack.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedOut) { if (state.loggedOut) onLoggedOut() }

    Scaffold(topBar = { TopAppBar(title = { Text("Perfil") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.loading) {
                CircularProgressIndicator()
            }
            state.email?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = {},
                    label = { Text("Correo") },
                    enabled = false,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            OutlinedTextField(state.fullName, viewModel::onFullName, label = { Text("Nombre completo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.nationalId, viewModel::onNationalId, label = { Text("Cédula / identificación") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.phone, viewModel::onPhone, label = { Text("Teléfono") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.address, viewModel::onAddress, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (state.saved) {
                Text("Perfil actualizado ✓", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }

            Button(onClick = viewModel::save, enabled = !state.saving, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar cambios")
            }
            OutlinedButton(onClick = viewModel::logout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}
