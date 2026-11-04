package cl.duoc.gestionprobadores.grupo9.view.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

val navEnterTransition: EnterTransition =
    slideInHorizontally(tween(250)) { it / 4 } + fadeIn(tween(250))

val navExitTransition: ExitTransition =
    slideOutHorizontally(tween(180)) { -it / 8 } + fadeOut(tween(180))

val navPopEnterTransition: EnterTransition =
    slideInHorizontally(tween(250)) { -it / 4 } + fadeIn(tween(250))

val navPopExitTransition: ExitTransition =
    slideOutHorizontally(tween(180)) { it / 8 } + fadeOut(tween(180))
