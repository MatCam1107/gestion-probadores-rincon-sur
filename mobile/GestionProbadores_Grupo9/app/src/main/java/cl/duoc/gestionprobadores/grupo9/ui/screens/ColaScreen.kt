package cl.duoc.gestionprobadores.grupo9.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.viewmodel.ColaViewModel

/**
 * Formulario validado (solo números) para registrar cuántos clientes están
 * esperando. El número grande usa AnimatedContent al cambiar y se muestra
 * un Snackbar al guardar.
 */
@Composable
fun ColaScreen(viewModel: ColaViewModel) {
    val estado by viewModel.uiState.collectAsState()
    val ultimo by viewModel.ultimoRegistro.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado.mostrarConfirmacion) {
        if (estado.mostrarConfirmacion) {
            snackbarHostState.showSnackbar("Registro de cola guardado")
            viewModel.confirmacionMostrada()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Clientes esperando", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = ultimo?.cantidadClientes ?: 0,
                transitionSpec = {
                    (slideInVertically { it } togetherWith slideOutVertically { -it })
                        .using(SizeTransform(clip = false))
                },
                label = "numeroCola"
            ) { cantidad ->
                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography.displayLarge
                )
            }
            Text("último registro", style = MaterialTheme.typography.labelMedium)

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = estado.cantidadTexto,
                onValueChange = viewModel::onCantidadChange,
                label = { Text("Nueva cantidad") },
                singleLine = true,
                isError = estado.errores.cantidad != null,
                supportingText = { estado.errores.cantidad?.let { Text(it) } },
                trailingIcon = {
                    if (estado.errores.cantidad != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = viewModel::registrar,
                enabled = !estado.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
