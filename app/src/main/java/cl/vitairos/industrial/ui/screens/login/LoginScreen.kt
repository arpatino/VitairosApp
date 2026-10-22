package cl.vitairos.industrial.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.vitairos.industrial.R
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.ui.components.PrimaryButton
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Hueso
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche
import cl.vitairos.industrial.ui.theme.VerdeProfundo

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(authRepository)),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.user) {
        if (uiState.user != null) {
            onLoginSuccess()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logotipo y Cabecera de la Marca
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vitairos_logo),
                    contentDescription = "Logo VITAIROS",
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "VITAIROS",
                        style = MaterialTheme.typography.titleLarge,
                        color = Hueso,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "INDUSTRIAL AI · TERMINAL MÓVIL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Oro,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Contexto
            Card(
                colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VerdeMineral, RoundedCornerShape(12.dp)),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACCESO OPERACIONAL EN FAENA",
                        style = MaterialTheme.typography.labelMedium,
                        color = Oro,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seleccione su perfil de demostración para iniciar el registro de reportes con persistencia local.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Niebla,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector Rápido de Perfiles
            Text(
                text = "PERFIL OPERACIONAL:",
                style = MaterialTheme.typography.labelSmall,
                color = Acero,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )

            // Tarjeta Operador
            RoleSelectCard(
                title = "Operador de Terreno",
                subtitle = "Juan Faena // Turno 1 (Mañana)",
                isSelected = uiState.selectedRole == UserRole.OPERATOR,
                activeColor = Cobre,
                onClick = { viewModel.selectRole(UserRole.OPERATOR) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta Supervisor
            RoleSelectCard(
                title = "Supervisor de Faena",
                subtitle = "Ing. Roberto Supervisor // Auditoría",
                isSelected = uiState.selectedRole == UserRole.SUPERVISOR,
                activeColor = Oro,
                onClick = { viewModel.selectRole(UserRole.SUPERVISOR) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Usuario
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Identificador de Usuario", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Acero) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cobre,
                    unfocusedBorderColor = VerdeMineral,
                    focusedTextColor = Acero,
                    unfocusedTextColor = Acero,
                    focusedLabelColor = Cobre,
                    unfocusedLabelColor = Acero,
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña / PIN", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Acero) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cobre,
                    unfocusedBorderColor = VerdeMineral,
                    focusedTextColor = Acero,
                    unfocusedTextColor = Acero,
                    focusedLabelColor = Cobre,
                    unfocusedLabelColor = Acero,
                ),
            )

            // Mensaje de Error Animado
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                uiState.error?.let { err ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Cobre, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = err, style = MaterialTheme.typography.bodySmall, color = Cobre)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Inicio
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Cobre)
            } else {
                PrimaryButton(
                    text = "INICIAR TERMINAL OPERACIONAL",
                    onClick = viewModel::signIn,
                )
            }
        }
    }
}

@Composable
private fun RoleSelectCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) VerdeProfundo else VerdeNoche,
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) activeColor else VerdeMineral,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Acero,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Niebla,
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = activeColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
