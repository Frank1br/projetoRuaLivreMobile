package br.edu.fatecpg.projetorualivremobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.fatecpg.projetorualivremobile.ui.screens.dashboard.DashboardScreen
import br.edu.fatecpg.projetorualivremobile.ui.screens.home.HomeScreen
import br.edu.fatecpg.projetorualivremobile.ui.screens.login.LoginScreen
import br.edu.fatecpg.projetorualivremobile.ui.screens.map.MapScreen
import br.edu.fatecpg.projetorualivremobile.ui.screens.profile.ProfileScreen
import br.edu.fatecpg.projetorualivremobile.ui.screens.register.RegisterScreen
import br.edu.fatecpg.projetorualivremobile.ui.screens.splash.SplashScreen

sealed class Route(val path: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object Map : Route("map")
    object Dashboard : Route("dashboard")
    object Profile : Route("profile")
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    eventsViewModel: AppEventsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Sessão expirou (401 num endpoint protegido) → volta para Login.
    LaunchedEffect(Unit) {
        eventsViewModel.sessionExpired.collect {
            snackbarHostState.showSnackbar("Sessão expirada. Faça login novamente.")
            navController.navigate(Route.Login.path) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Erros de rede globais → snackbar.
    LaunchedEffect(Unit) {
        eventsViewModel.errors.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Route.Splash.path) {

            composable(Route.Splash.path) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Route.Login.path) {
                            popUpTo(Route.Splash.path) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Splash.path) { inclusive = true }
                        }
                    }
                )
            }

            composable(Route.Login.path) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.Register.path) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Route.Home.path) {
                HomeScreen(
                    onNavigateToMap = { navController.navigate(Route.Map.path) },
                    onNavigateToDashboard = { navController.navigate(Route.Dashboard.path) },
                    onNavigateToProfile = { navController.navigate(Route.Profile.path) }
                )
            }

            composable(Route.Map.path) {
                MapScreen(
                    onNavigateToHome = { navController.navigate(Route.Home.path) },
                    onNavigateToDashboard = { navController.navigate(Route.Dashboard.path) },
                    onNavigateToProfile = { navController.navigate(Route.Profile.path) }
                )
            }

            composable(Route.Dashboard.path) {
                DashboardScreen(
                    onNavigateToHome = { navController.navigate(Route.Home.path) },
                    onNavigateToMap = { navController.navigate(Route.Map.path) },
                    onNavigateToProfile = { navController.navigate(Route.Profile.path) }
                )
            }

            composable(Route.Profile.path) {
                ProfileScreen(
                    onNavigateToHome = { navController.navigate(Route.Home.path) },
                    onNavigateToMap = { navController.navigate(Route.Map.path) },
                    onNavigateToDashboard = { navController.navigate(Route.Dashboard.path) },
                    onLogout = {
                        navController.navigate(Route.Login.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
