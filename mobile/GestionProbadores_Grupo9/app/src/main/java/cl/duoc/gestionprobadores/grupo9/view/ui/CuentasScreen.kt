package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.Usuario

@Composable
fun CuentasScreen(
    usuarios: List<Usuario>,
    nuevaCuenta: () -> Unit,
    editarCuenta: (Int) -> Unit,
    cambiarEstado: (Usuario) -> Unit
) {
    val colaboradores = usuarios.filter { !it.esAdministrador() }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Cuentas de colaboradores", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Crea cuentas y asigna las sucursales donde trabaja cada persona.")
            Button(onClick = nuevaCuenta, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Nueva cuenta") }
            Text("${colaboradores.size} colaboradores · ${colaboradores.count { it.activo }} activos", modifier = Modifier.padding(top = 10.dp))
        }
        items(colaboradores, key = { it.id }) { usuario ->
            Card(onClick = { editarCuenta(usuario.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(usuario.nombre, fontWeight = FontWeight.Bold)
                        Text(usuario.email)
                        Text(usuario.listaSucursales().joinToString(), style = MaterialTheme.typography.labelMedium)
                    }
                    Switch(checked = usuario.activo, onCheckedChange = { cambiarEstado(usuario) })
                }
            }
        }
    }
}
