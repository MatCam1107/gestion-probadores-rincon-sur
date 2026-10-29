package cl.duoc.gestionprobadores.grupo9.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.viewmodel.ColaViewModel

@Composable
fun ColaScreen(
    viewModel: ColaViewModel
) {
    val cantidadClientes by viewModel.cantidadClientes.collectAsState()
    val probadoresLibres by viewModel.probadoresLibres.collectAsState()
    val tiempoEstimado by viewModel.tiempoEstimado.collectAsState()
    val registros by viewModel.registros.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(mensaje) {
        mensaje?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Cola de espera",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Registra cuántos clientes esperan un probador",
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Clientes esperando ahora",
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = viewModel::disminuirClientes,
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "−",
                                    style =
                                        MaterialTheme.typography.headlineSmall
                                )
                            }

                            Text(
                                text = cantidadClientes.toString(),
                                style =
                                    MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = viewModel::aumentarClientes,
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "+",
                                    style =
                                        MaterialTheme.typography.headlineSmall
                                )
                            }
                        }

                        Text(
                            text = "Probadores libres ahora: $probadoresLibres",
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Text(
                            text = "Espera estimada: $tiempoEstimado min",
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::guardarRegistro,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Guardar registro")
                }
            }

            item {
                Text(
                    text = "Registros de hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                items = registros,
                key = { registro -> registro.id }
            ) { registro ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = registro.hora)

                        Text(
                            text =
                                "${registro.cantidadClientes} clientes",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}