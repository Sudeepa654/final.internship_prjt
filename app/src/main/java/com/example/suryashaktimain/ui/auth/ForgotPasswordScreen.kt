package com.example.suryashaktimain.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.SolarTextField
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.AuthUiState

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    onResetPassword: (String, String) -> Unit,
    onBackToLogin: () -> Unit,
    onMessageShown: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        if (uiState.errorMessage != null || uiState.successMessage != null) {
            kotlinx.coroutines.delay(3500)
            onMessageShown()
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = SolarYellow)
            Text("Reset Password", color = SolarText, style = MaterialTheme.typography.headlineSmall)
            Text("Updates the password stored in Room", color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(20.dp))

            AuthCard {
                SolarTextField(value = email, onValueChange = { email = it }, label = "Registered email")
                SolarTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New password",
                    visualTransformation = PasswordVisualTransformation()
                )
                MessageText(uiState)
                Button(
                    onClick = { onResetPassword(email, newPassword) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarYellow,
                        contentColor = SolarBlack
                    )
                ) {
                    Text(if (uiState.isLoading) "Saving..." else "Update password", fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = onBackToLogin) {
                Text("Back to login", color = SolarYellow)
            }
        }
    }
}

