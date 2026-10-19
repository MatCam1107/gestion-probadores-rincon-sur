package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.gestionprobadores.grupo9.model.Probador
import cl.duoc.gestionprobadores.grupo9.repository.ProbadorRepository

class ProbadorViewModel(
    private val repository: ProbadorRepository = ProbadorRepository()
) : ViewModel() {

    var probadores: List<Probador> by mutableStateOf(
        repository.obtenerProbadores()
    )
        private set
}