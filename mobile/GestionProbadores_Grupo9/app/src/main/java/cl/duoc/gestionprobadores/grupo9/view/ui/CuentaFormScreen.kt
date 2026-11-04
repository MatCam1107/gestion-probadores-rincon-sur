package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.Usuario

@Composable
fun CuentaFormScreen(
    usuario: Usuario?,
    esNueva: Boolean,
    guardar: (String, String, String, List<String>, Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }
    val sucursales = remember { mutableStateListOf<String>() }

    LaunchedEffect(usuario?.id) {
        if (usuario != null) {
            nombre = usuario.nombre
            email = usuario.email
            activo = usuario.activo
            sucursales.clear()
            sucursales.addAll(usuario.listaSucursales())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("Correo corporativo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            contrasena,
            { contrasena = it },
            label = { Text(if (esNueva) "Contraseña que usará" else "Nueva contraseña (opcional)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Text("Sucursales donde trabaja")
        SUCURSALES.forEach { sucursal ->
            FilterChip(
                selected = sucursal in sucursales,
                onClick = { if (sucursal in sucursales) sucursales.remove(sucursal) else sucursales.add(sucursal) },
                label = { Text(sucursal) }
            )
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Cuenta activa", modifier = Modifier.weight(1f))
            Switch(checked = activo, onCheckedChange = { activo = it })
        }
        Button(onClick = { guardar(nombre, email, contrasena, sucursales.toList(), activo) }, modifier = Modifier.fillMaxWidth()) {
            Text(if (esNueva) "Crear cuenta" else "Guardar cambios")
        }
    }
}
