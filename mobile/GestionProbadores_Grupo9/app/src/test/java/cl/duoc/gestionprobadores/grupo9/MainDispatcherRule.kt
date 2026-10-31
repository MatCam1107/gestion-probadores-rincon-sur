package cl.duoc.gestionprobadores.grupo9

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regla de JUnit que reemplaza Dispatchers.Main por un TestDispatcher
 * mientras corre cada test. viewModelScope usa Dispatchers.Main por
 * dentro, así que sin esto los tests de ViewModel no funcionan.
 *
 * Se usa UnconfinedTestDispatcher (no StandardTestDispatcher) para que las
 * coroutines lanzadas con viewModelScope.launch corran de inmediato, sin
 * necesitar un TestCoroutineScheduler compartido con el runTest { } de cada
 * test (evita un error clásico de sincronización entre schedulers).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        kotlinx.coroutines.Dispatchers.resetMain()
    }
}
