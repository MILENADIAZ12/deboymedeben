package com.tuempresa.deboymedebn.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tuempresa.deboymedebn.ui.screens.*
import com.tuempresa.deboymedebn.viewmodel.AuthViewModel
import com.tuempresa.deboymedebn.viewmodel.ContactsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    contactsViewModel: ContactsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // -------- LOGIN --------
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // -------- REGISTER --------
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // -------- MAIN --------
        composable("main") {
            MainScreen(
                contactsViewModel = contactsViewModel,
                authViewModel = authViewModel,   //  AHORA SÍ
                currentScreen = "home",
                onAddClick = {
                    navController.navigate("addContact")
                },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onLogout = {
                    authViewModel.logout()          // cerrar sesión
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        // -------- CALENDAR --------
        composable("calendar") {
            val contacts by contactsViewModel.contacts.collectAsState()
            CalendarScreen(
                contacts = contacts,
                onBack = { navController.popBackStack() }
            )
        }

        // -------- NOTIFICATIONS --------
        composable("notifications") {
            NotificationsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // -------- ADD CONTACT --------
        composable("addContact") {
            AddContactScreen(
                contactsViewModel = contactsViewModel,
                onSaveDone = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}
