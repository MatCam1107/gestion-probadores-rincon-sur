package cl.duoc.gestionprobadores.grupo9.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.viewmodel.LoginViewModel

/** Pantalla de registro de un nuevo usuario (Vendedor o Supervisor). */
@Composable
fun RegistroScreen(
    viewModel: LoginViewModel,
    onRegistroExitoso: () -> Unit,
    onVolverALogin: () -> Unit
) {
    val estado by viewModel.registroUiState.collectAsState()

    LaunchedEffect(estado.registroExitoso) {
        if (estado.registroExitoso) onRegistroExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onRegistroNombreChange,
            label = { Text("Nombre completo") },
            singleLine = true,
            isError = estado.errores.nombre != null,
            supportingText = { estado.errores.nombre?.let { Text(it) } },
            trailingIcon = { if (estado.errores.nombre != null) Icon(Icons.Default.Error, null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onRegistroCorreoChange,
            label = { Text("Correo") },
            singleLine = true,
            isError = estado.errores.correo != null,
            supportingText = { estado.errores.correo?.let { Text(it) } },
            trailingIcon = { if (estado.errores.correo != null) Icon(Icons.Default.Error, null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onRegistroClaveChange,
            label = { Text("Clave") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.errores.clave != null,
            supportingText = { estado.errores.clave?.let { Text(it) } },
            trailingIcon = { if (estado.errores.clave != null) Icon(Icons.Default.Error, null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.confirmarClave,
            onValueChange = viewModel::onRegistroConfirmarClaveChange,
            label = { Text("Confirmar clave") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.errores.confirmarClave != null,
            supportingText = { estado.errores.confirmarClave?.let { Text(it) } },
            trailingIcon = { if (estado.errores.confirmarClave != null) Icon(Icons.Default.Error, null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Text("Rol", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Rol.entries.forEach { rol ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = estado.rol == rol,
                            onClick = { viewModel.onRegistroRolChange(rol) }
                        )
                        .padding(end = 16.dp)
                ) {
                    RadioButton(selected = estado.rol == rol, onClick = { viewModel.onRegistroRolChange(rol) })
                    Text(if (rol == Rol.VENDEDOR) "Vendedor" else "Supervisor")
                }
            }
        }

        AnimatedVisibility(
            visible = estado.errorGeneral != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = estado.errorGeneral ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = viewModel::registrar,
            enabled = !estado.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Crear cuenta")
            }
        }

        TextButton(onClick = onVolverALogin, modifier = Modifier.fillMaxWidth()) {
            Text("Ya tengo cuenta, volver")
        }
    }
}
