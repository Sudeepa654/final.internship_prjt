package com.example.suryashaktimain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.suryashaktimain.data.local.AppDatabase
import com.example.suryashaktimain.data.repository.SolarRepository
import com.example.suryashaktimain.navigation.SuryaShaktiApp
import com.example.suryashaktimain.ui.theme.SuryaShaktiTheme
import com.example.suryashaktimain.viewmodel.AuthViewModel
import com.example.suryashaktimain.viewmodel.EnergyViewModel
import com.example.suryashaktimain.viewmodel.SolarViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current.applicationContext
            val repository = remember {
                val database = AppDatabase.getDatabase(context)
                SolarRepository(
                    userDao = database.userDao(),
                    energyLogDao = database.energyLogDao()
                )
            }
            val factory = remember { SolarViewModelFactory(repository) }
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            val energyViewModel: EnergyViewModel = viewModel(factory = factory)

            SuryaShaktiTheme {
                SuryaShaktiApp(
                    authViewModel = authViewModel,
                    energyViewModel = energyViewModel
                )
            }
        }
    }
}

