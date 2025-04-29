package dev.nalamzap.weatheringwithyou.feature_preference

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.nalamzap.weatheringwithyou.MainActivity
import dev.nalamzap.weatheringwithyou.R
import dev.nalamzap.weatheringwithyou.ui.theme.WeatheringWithYouTheme

class PreferenceActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasCity()) {
            Log.d(TAG, "onCreate: has city")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent {
            WeatheringWithYouTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Preference(modifier = Modifier.padding(innerPadding), saveCityAndNavigate = {
                        saveCity(it)
                        startActivity(Intent(this, MainActivity::class.java))
                    })
                }
            }
        }
    }

    private fun hasCity(): Boolean {
        val sharedPreferences = getSharedPreferences("WeatheringWithYou", MODE_PRIVATE)
        return sharedPreferences.contains(CITY)
    }

    private fun saveCity(city: String) {
        val sharedPreferences = getSharedPreferences("WeatheringWithYou", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(CITY, city)
        editor.apply()
    }

    companion object {
        const val TAG = "PreferenceActivity"
        const val CITY = "city"
    }
}

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    saveCityAndNavigate: (String) -> Unit = {}
) {
    val city = remember { mutableStateOf("") }
    Column {
        Spacer(modifier = modifier.height(100.dp))
        Text(
            text = stringResource(R.string.app_name),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(2f))
        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Enter your preferred city")
            },
            value = city.value,
            onValueChange = {
                city.value = it
            }
        )
        Row {
            Spacer(modifier = Modifier.weight(1f))
            ElevatedButton(
                onClick = {
                    saveCityAndNavigate(city.value)
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f)),
                modifier = Modifier
                    .padding(16.dp)
                    .width(200.dp),
            ) {
                Text(text = stringResource(R.string.proceed))
            }
        }
        Spacer(modifier = Modifier.weight(3.5f))
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencePreview(modifier: Modifier = Modifier) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Preference(modifier = Modifier.padding(innerPadding))
    }
}