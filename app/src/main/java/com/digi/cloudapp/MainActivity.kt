package com.digi.cloudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.digi.cloudapp.ui.theme.CloudAppTheme
import com.digi.cloudapp.ui.theme.UiState

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

                }
            }
        }
    }
}

@Composable
fun ChoreScreen(
    state: UiState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Row {
                OutlinedTextField(value = state.name, onValueChange = {})
                Button(onClick = { }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChoreScreenPreview() {
    CloudAppTheme {
        ChoreScreen()
    }
}