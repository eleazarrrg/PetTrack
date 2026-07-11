package com.pettrack.app.ui.pets.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pettrack.app.domain.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(
    onAddPet: () -> Unit,
    onEditPet: (String) -> Unit,
    viewModel: MyReportsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Reload whenever the screen resumes (e.g. after creating/editing a pet).
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis reportes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPet) {
                Icon(Icons.Filled.Add, contentDescription = "Reportar mascota")
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                state.pets.isEmpty() -> Text(
                    "Aún no has reportado mascotas.\nToca + para crear una.",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.pets, key = { it.id }) { pet ->
                        PetRow(pet = pet, onClick = { onEditPet(pet.id) }, onDelete = { viewModel.delete(pet.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PetRow(pet: Pet, onClick: () -> Unit, onDelete: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(pet.name, style = MaterialTheme.typography.titleMedium)
            Text(
                buildString {
                    append(pet.species)
                    pet.breed?.let { append(" · $it") }
                },
                style = MaterialTheme.typography.bodyMedium,
            )
            Column {
                AssistChip(onClick = {}, label = { Text(pet.status.label) })
            }
            IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
