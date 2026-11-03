package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.viewmodels.LoginUiState

@Composable
fun LoginScreen(
    estado: LoginUiState,
    cambiarEmail: (String) -> Unit,
    cambiarContrasena: (String) -> Unit,
    seleccionarSucursal: (String) -> Unit,
    usarCuenta: (String) -> Unit,
    iniciarSesion: () -> Unit
) {
    var mostrar by remember { mutableStateOf(false) }
    var recordar by remember { mutableStateOf(true) }
    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            Text("Rincón Sur", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(
                if (estado.email.equals("admin@rinconsur.cl", true)) "Acceso de administración" else "¡Bienvenido, colaborador!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text("Uso interno para la gestión de probadores", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = estado.email,
                onValueChange = cambiarEmail,
                label = { Text("Correo corporativo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.contrasena,
                onValueChange = cambiarContrasena,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (mostrar) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { mostrar = !mostrar }) { Text(if (mostrar) "Ocultar" else "Ver") } },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            )
            if (estado.sucursalesDisponibles.isNotEmpty()) {
                SelectorSucursal(
                    seleccionada = estado.sucursalSeleccionada,
                    opciones = estado.sucursalesDisponibles,
                    alSeleccionar = seleccionarSucursal,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = recordar, onCheckedChange = { recordar = it })
                Text("Recordar mi correo")
            }
            estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) }
            Button(
                onClick = iniciarSesion,
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
            ) {
                if (estado.cargando) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(22.dp))
                else Text("Iniciar sesión")
            }
            Text("Cuentas de demostración · clave 12345678", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { usarCuenta("ana.rojas@rinconsur.cl") }, modifier = Modifier.weight(1f)) { Text("Ana") }
                OutlinedButton(onClick = { usarCuenta("carlos.perez@rinconsur.cl") }, modifier = Modifier.weight(1f)) { Text("Carlos") }
                OutlinedButton(onClick = { usarCuenta("admin@rinconsur.cl") }, modifier = Modifier.weight(1f)) { Text("Admin") }
            }
        }
    }
}
