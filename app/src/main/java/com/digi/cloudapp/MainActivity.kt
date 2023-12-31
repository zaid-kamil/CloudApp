package com.digi.cloudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.digi.cloudapp.data.Chores
import com.digi.cloudapp.ui.theme.ChoreViewModel
import com.digi.cloudapp.ui.theme.CloudAppTheme
import com.digi.cloudapp.ui.theme.DownloadStatus
import com.digi.cloudapp.ui.theme.UiState
import com.digi.cloudapp.ui.theme.UploadStatus

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CloudAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ChoreViewModel = viewModel()
                    val uiState = viewModel.uiState.collectAsState().value
                    ChoreScreen(state = uiState, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}

sealed interface ChoreEvent {
    object OnSaveClicked : ChoreEvent
    object OnRefreshClicked : ChoreEvent
    data class OnNameEdit(val name: String) : ChoreEvent
    data class OnItemDelete(val chore: Chores) : ChoreEvent
}

@Composable
fun ChoreScreen(
    state: UiState,
    onEvent: (ChoreEvent) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.Blue.copy(alpha = 0.2f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                TextField(
                    label = { Text(text = "Add a chore") },
                    placeholder = { Text(text = "Throw the Trash") },
                    value = state.name,
                    onValueChange = { onEvent(ChoreEvent.OnNameEdit(it)) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        if (state.uploadStatus == UploadStatus.IN_PROGRESS) {
                            CircularProgressIndicator(strokeWidth = 3.dp)
                        }
                    }
                )
                IconButton(
                    onClick = { onEvent(ChoreEvent.OnSaveClicked) },
                    enabled = state.name.isNotEmpty(),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = state.errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Text(
                        text = "All Chores",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    if (state.downloadStatus == DownloadStatus.IN_PROGRESS) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(24.dp)
                        )
                    }
                }
                IconButton(onClick = { onEvent(ChoreEvent.OnRefreshClicked) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "refresh"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                when (state.downloadStatus) {
                    DownloadStatus.IN_PROGRESS -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        }
                    }

                    DownloadStatus.SUCCESS -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(state.choresList) { chore ->
                                ChoreCardItem(
                                    chore = chore,
                                    onDelete = { onEvent(ChoreEvent.OnItemDelete(chore)) }
                                )
                            }
                        }
                    }

                    DownloadStatus.FAILURE -> {
                        Text(
                            text = "Failed to fetch chores",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {}
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreCardItem(chore: Chores, onDelete: () -> Unit) {
    Card(onClick = { /*TODO*/ }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = chore.name, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CloudAppTheme {
        ChoreCardItem(chore = Chores("Throw the trash")) {}
    }
}

@Preview(showBackground = true)
@Composable
fun ChoreScreenPreview() {
    CloudAppTheme {
        ChoreScreen(state = UiState())
    }
}