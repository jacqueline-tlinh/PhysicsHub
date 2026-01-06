package com.example.physicshub.ui.screens.login
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.theme.PhysicsHubTheme
import com.example.physicshub.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
)
 {
    val uiState by viewModel.uiState

    // React to login success
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
            viewModel.consumeLoginSuccess()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(70.dp))

            // Title
            Text(
                text = "PhysicsHub",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Your central hub for announcements, events, and exam archives.",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logo
            Image(
                painter = painterResource(R.drawable.physicshub_logo),
                contentDescription = "PhysicsHub logo",
                modifier = Modifier.size(158.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input box
            Column(modifier = Modifier.fillMaxWidth()) {

                OutlinedTextField(
                    value = uiState.studentId,
                    onValueChange = viewModel::onStudentIdChange,
                    label = { Text("Student ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Password: initials (lowercase) followed by the last 4 digits of your ID.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login button
            Button(
                onClick = viewModel::login,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Log in",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

//            TextButton(
//                onClick = viewModel::devLogin
//            ) {
//                Text("DEV: Skip login")
//            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PhysicsHubTheme {
        LoginScreen(
            onLoginSuccess = {}
        )
    }
}
