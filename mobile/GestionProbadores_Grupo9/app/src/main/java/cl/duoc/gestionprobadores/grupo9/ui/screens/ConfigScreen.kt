package cl.duoc.gestionprobadores.grupo9.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.ui.components.InterruptorAnimado
import cl.duoc.gestionprobadores.grupo9.viewmodel.ConfigViewModel

/**
 * Configuración: umbral de minutos (validado), switches de notificaciones y
 * vibración (persistidos en DataStore) y cerrar sesión.
 */
@Composable
fun ConfigScreen(viewModel: ConfigViewModel, onSesionCerrada: () -> Unit) {
    val config by viewModel.config.collectAsState()
    val umbralEstado by viewModel.umbralUiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Configuración", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        Text("Umbral de tiempo excedido (minutos)", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Top) {
            OutlinedTextField(
                value = umbralEstado.texto,
                onValueChange = viewModel::onUmbralTextoChange,
                isError = umbralEstado.error != null,
                supportingText = { umbralEstado.error?.let { Text(it) } },
                trailingIcon = {
                    if (umbralEstado.error != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Button(onClick = viewModel::guardarUmbral, modifier = Modifier.padding(top = 4.dp)) {
                Text("Guardar")
            }
        }
        if (umbralEstado.guardadoOk) {
            Text(
                "Umbral actualizado a ${config.umbralMinutos} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        FilaInterruptor(
            titulo = "Notificaciones",
            activo = config.notificacionesActivas,
            onCambio = viewModel::actualizarNotificaciones
        )
        Spacer(Modifier.height(16.dp))
        FilaInterruptor(
            titulo = "Vibración",
            activo = config.vibracionActiva,
            onCambio = viewModel::actualizarVibracion
        )

        Spacer(Modifier.height(40.dp))
        OutlinedButton(
            onClick = {
                viewModel.cerrarSesion()
                onSesionCerrada()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun FilaInterruptor(titulo: String, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo, style = MaterialTheme.typography.bodyLarge)
        InterruptorAnimado(activo = activo, onCambio = onCambio)
    }
}
