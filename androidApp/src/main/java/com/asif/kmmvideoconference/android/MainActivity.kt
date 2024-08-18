package com.asif.kmmvideoconference.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asif.kmmvideoconference.MeetingViewModelFactory
import com.asif.kmmvideoconference.android.viewmodels.MeetingViewModel
import com.asif.kmmvideoconference.repository.ChimeRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

class MainActivity : ComponentActivity() {
    // Provide the ViewModel with the factory
    private lateinit var meetingViewModel: MeetingViewModel

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    // Register the permission launcher using the Activity Result API
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions if not already granted
        if (!hasPermissions(REQUIRED_PERMISSIONS)) {
            requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
        }

        // Initialize HttpClient instance with CIO engine
        val httpClient = HttpClient(CIO)

        // Initialize the ChimeRepository and Context
        val repository = ChimeRepository(httpClient)
        val factory = MeetingViewModelFactory(repository, applicationContext)

        // Instantiate ViewModel
        meetingViewModel = ViewModelProvider(this, factory).get(MeetingViewModel::class.java)

        setContent {
            // Create a NavController to handle navigation between screens
            val navController = rememberNavController()

            // Setup navigation using NavHost
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController, meetingViewModel) } // HomeScreen is the start screen
                composable("meeting") { MeetingScreen(meetingViewModel) } // Navigate to MeetingScreen
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
