# Gestión de Probadores · Tienda Rincón Sur

App móvil interna para el personal de tienda: muestra en tiempo real qué
probadores están libres, ocupados o con tiempo excedido, cuánto tiempo
llevan ocupados, cuántos clientes están esperando en fila, y permite
reportar prendas olvidadas (con foto) y revisar el historial de uso.

Proyecto académico para **DSY1105 Desarrollo de Aplicaciones Móviles**
(DuocUC) — Encargo (Evaluación Parcial 2) y defensa individual (Evaluación
Parcial 3). Datos ficticios, sin backend real.

## Integrantes

- Nicolás Rojas Barría
- Elvira San Martín

## Problema que resuelve

El personal de la tienda no puede ver desde el pasillo qué probadores
están libres u ocupados, ni hace cuánto tiempo. Gestión de Probadores
resuelve esto con:

- Panel visual del estado de cada probador (libre / ocupado / excedido).
- Alertas automáticas cuando un probador supera un tiempo límite
  configurable.
- Reporte de prendas olvidadas, con foto tomada desde la app.
- Registro de cuántos clientes están esperando.
- Login con roles **Vendedor** y **Supervisor**.

## Tecnologías

- Kotlin 2.2.10 + Jetpack Compose + Material 3
- Arquitectura **MVVM** (Model · Repository · ViewModel · Screens/Composables)
- **StateFlow** para estado reactivo, con validación de formularios
  desacoplada en el ViewModel
- **Navigation Compose** (NavHost + barra de navegación inferior)
- **Room** (SQLite) para persistencia local — 6 entidades con CRUD real
- **DataStore Preferences** para configuración simple (umbral de minutos,
  switches)
- Recursos nativos: **cámara** (FileProvider), **vibración** y
  **notificaciones**
- Animaciones: `animateColorAsState`, `rememberInfiniteTransition`,
  `animateFloatAsState`, `animateDpAsState`, `AnimatedVisibility`,
  `AnimatedContent`
- AGP 9 con **Kotlin built-in** (sin plugin `kotlin-android`) + **KSP**
  para el compilador de Room

## Estructura del proyecto

```
app/src/main/java/cl/duoc/gestionprobadores/grupo9/
├── model/        # Data classes = entidades Room (@Entity)
├── data/         # AppDatabase, DAOs, Converters, ConfigPreferences (DataStore)
├── repository/   # Interfaz + implementación Room de cada dominio
├── viewmodel/    # UiState + StateFlow + validación, uno por pantalla (o compartido)
├── ui/
│   ├── screens/      # Composables de cada pantalla
│   ├── components/   # Piezas reutilizables (anillo de tiempo, switch animado, formulario de prenda)
│   ├── navigation/    # NavGraph y rutas
│   └── theme/         # Material 3, color base #1B4965
└── util/         # AppContainer (DI manual), SesionManager, helpers de cámara/vibración/notificaciones
```

## Pantallas

| Pantalla | ViewModel | Qué hace |
|---|---|---|
| Login / Registro | `LoginViewModel` | Autenticación simulada contra Room, roles Vendedor/Supervisor |
| Panel | `PanelViewModel` | Grilla de probadores, color según estado, ícono parpadeante si excede el tiempo |
| Detalle | `PanelViewModel` | Ficha de un probador: anillo de tiempo, marcar libre/ocupado, reportar prenda olvidada |
| Cola | `ColaViewModel` | Registra cuántos clientes esperan (formulario numérico validado) |
| Alertas | `AlertasViewModel` | Historial de alertas (tiempo excedido / prenda olvidada), marcar atendidas |
| Historial | `HistorialViewModel` | Promedio general y por probador de minutos de ocupación |
| Config | `ConfigViewModel` | Umbral de minutos, notificaciones, vibración, cerrar sesión |

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en **Android Studio** (versión compatible
   con AGP 9 / Kotlin 2.2.10).
2. Dejar que Gradle sincronice (descarga Room, Navigation, DataStore, KSP).
3. Ejecutar en un emulador o dispositivo con **Android 13 (API 33) o
   superior** (es el `minSdk` del proyecto).
4. En el primer inicio la app precarga 10 probadores y dos cuentas de
   demostración:
   - `vendedor@rinconsur.cl` / `12345678` (rol Vendedor)
   - `supervisor@rinconsur.cl` / `12345678` (rol Supervisor)
5. Al abrir el Panel, la app pedirá permiso de notificaciones (Android 13+);
   al tocar "Tomar foto" en el formulario de prenda olvidada, pedirá permiso
   de cámara.

## Estado actual

Base de arquitectura, Room, navegación, las 7 pantallas, animaciones,
formularios validados y los 3 recursos nativos (cámara, vibración,
notificaciones) implementados de punta a punta. Pendiente: pulir detalles
visuales finales y agregar más pruebas unitarias antes de la entrega.
