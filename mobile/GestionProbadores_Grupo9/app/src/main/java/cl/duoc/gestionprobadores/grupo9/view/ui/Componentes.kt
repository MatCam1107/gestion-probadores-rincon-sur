package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.view.theme.FondoClausurado
import cl.duoc.gestionprobadores.grupo9.view.theme.FondoExcedido
import cl.duoc.gestionprobadores.grupo9.view.theme.FondoLibre
import cl.duoc.gestionprobadores.grupo9.view.theme.FondoOcupado
import cl.duoc.gestionprobadores.grupo9.view.theme.FondoRevisar

val SUCURSALES = listOf("Plaza Norte", "Mall Vespucio", "Mall Tobalaba", "Costanera Center")

fun textoEstado(estado: EstadoProbador): String = when (estado) {
    EstadoProbador.LIBRE -> "Libre"
    EstadoProbador.OCUPADO -> "Ocupado"
    EstadoProbador.EXCEDIDO -> "Excedido"
    EstadoProbador.REVISAR -> "Revisar"
    EstadoProbador.CLAUSURADO -> "Clausurado"
}

fun colorEstado(estado: EstadoProbador): Color = when (estado) {
    EstadoProbador.LIBRE -> FondoLibre
    EstadoProbador.OCUPADO -> FondoOcupado
    EstadoProbador.EXCEDIDO -> FondoExcedido
    EstadoProbador.REVISAR -> FondoRevisar
    EstadoProbador.CLAUSURADO -> FondoClausurado
}

@Composable
fun SelectorSucursal(
    seleccionada: String,
    opciones: List<String>,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }
    androidx.compose.foundation.layout.Box(modifier) {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Text(if (seleccionada == "Todas") "Todas las sucursales" else seleccionada)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { sucursal ->
                DropdownMenuItem(
                    text = { Text(if (sucursal == "Todas") "Todas las sucursales" else sucursal) },
                    onClick = {
                        abierto = false
                        alSeleccionar(sucursal)
                    }
                )
            }
        }
    }
}

@Composable
fun TarjetaResumen(titulo: String, cantidad: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = Color(0xFF1F1F1F)
        )
    ) {
        androidx.compose.foundation.layout.Column(Modifier.padding(12.dp)) {
            Text(cantidad.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun DialogoContador(
    titulo: String,
    valorInicial: Int,
    minimo: Int,
    maximo: Int,
    confirmar: String,
    alCerrar: () -> Unit,
    alConfirmar: (Int) -> Unit
) {
    var valor by remember { mutableStateOf(valorInicial.coerceIn(minimo, maximo)) }
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text(titulo) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { valor = (valor - 1).coerceAtLeast(minimo) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar")
                }
                Text(valor.toString(), style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(horizontal = 18.dp))
                IconButton(onClick = { valor = (valor + 1).coerceAtMost(maximo) }) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar")
                }
            }
        },
        confirmButton = { Button(onClick = { alConfirmar(valor) }) { Text(confirmar) } },
        dismissButton = { OutlinedButton(onClick = alCerrar) { Text("Cancelar") } }
    )
}
