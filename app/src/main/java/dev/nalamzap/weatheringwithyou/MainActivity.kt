package dev.nalamzap.weatheringwithyou

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.nalamzap.weatheringwithyou.feature_preference.PreferenceActivity
import dev.nalamzap.weatheringwithyou.ui.theme.WeatheringWithYouTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var cityPref: String

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            App.instance.weatherRepo,
            getCity() ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        cityPref = getCity() ?: ""

        setContent {
            WeatheringWithYouTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        saveCity = {
                            saveCity(it)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun saveCity(city: String) {
        val sharedPreferences = getSharedPreferences("WeatheringWithYou", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(PreferenceActivity.CITY, city)
        editor.apply()
    }

    private fun getCity(): String? {
        val sharedPreferences = getSharedPreferences("WeatheringWithYou", MODE_PRIVATE)
        return sharedPreferences.getString(PreferenceActivity.CITY, "")
    }

    companion object{
        const val TAG = "MainActivity"
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    saveCity: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var scrim = remember { mutableStateOf(false) }
    var newCity = remember { mutableStateOf(viewModel.city) }
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.app_name),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Row(modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Rounded.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.primary)
            Text(viewModel.city, modifier = Modifier.padding(horizontal = 8.dp))
            ElevatedButton(onClick = {
                scrim.value = true
            }) {
                Text(text = "Change")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        when {
            viewModel.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.CenterHorizontally))
            }
            viewModel.errorMessage != null -> {
                Text(
                    text = viewModel.errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            viewModel.weatherState != null -> {
                val weather = viewModel.weatherState!!
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Temperature: ${weather.temperature}°C")
                    Text(text = "Condition: ${weather.condition}")
                    Text(text = "Updated at: ${formatTimestamp(weather.timestamp)}")
                }
            }
            else -> {
                Text(
                    text = "No data available",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
    if (scrim.value) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Card(modifier = Modifier.padding(16.dp)) {
                Column {
                    Text(text = "Enter new city",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        value = newCity.value,
                        onValueChange = {
                            newCity.value = it
                        }
                    )
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                scrim.value = false
                            },
                            modifier = Modifier.width(150.dp)) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                saveCity(newCity.value)
                                viewModel.updateCity(newCity.value)
                                scrim.value = false
                            },
                            modifier = Modifier.width(150.dp)) {
                            Text(text = "Save")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

fun formatTimestamp(timestampMillis: Long): String {
    Log.d("MainActivity", "formatTimestamp: $timestampMillis")
    val timeAndDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        .format(timestampMillis * 1000)

    return timeAndDate
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    var scrim = remember { mutableStateOf(true) }
    WeatheringWithYouTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Weathering With You",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Row(modifier = Modifier.padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Preview City", modifier = Modifier.padding(horizontal = 8.dp))
                    ElevatedButton(onClick = {
                    }) {
                        Text(text = "Change")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Temperature: 24.5°C")
                    Text(text = "Condition: Sunny")
                    Text(text = "Updated at: 28 Apr 2025, 04:45 PM")
                }
                Spacer(modifier = Modifier.weight(1.3f))
            }
            if (scrim.value) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            scrim.value = false
                        }
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Card(modifier = Modifier.padding(16.dp)) {
                        Column {
                            Text(text = "Enter new city",
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                            OutlinedTextField(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                value = "Preview City",
                                onValueChange = {

                                }
                            )
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        scrim.value = false
                                    },
                                    modifier = Modifier.width(150.dp)) {
                                    Text(text = "Cancel")
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {

                                    },
                                    modifier = Modifier.width(150.dp)) {
                                    Text(text = "Save")
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}