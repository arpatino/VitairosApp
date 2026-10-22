package cl.vitairos.industrial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.vitairos.industrial.di.AppContainer
import cl.vitairos.industrial.ui.screens.capture.CaptureScreen
import cl.vitairos.industrial.ui.screens.dashboard.DashboardScreen
import cl.vitairos.industrial.ui.screens.history.HistoryScreen
import cl.vitairos.industrial.ui.screens.login.LoginScreen
import cl.vitairos.industrial.ui.screens.newreport.NewReportScreen
import cl.vitairos.industrial.ui.screens.queue.QueueScreen
import cl.vitairos.industrial.ui.screens.reportdetail.ReportDetailScreen
import cl.vitairos.industrial.ui.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentUser by container.authRepository.currentUser.collectAsStateWithLifecycle(initialValue = null)

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authRepository = container.authRepository,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                authRepository = container.authRepository,
                reportRepository = container.reportRepository,
                connectivityObserver = container.connectivity,
                onNavigateToNewReport = {
                    navController.navigate(Routes.NEW_REPORT)
                },
                onNavigateToQueue = {
                    navController.navigate(Routes.QUEUE)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.NEW_REPORT) {
            NewReportScreen(
                catalogRepository = container.catalogRepository,
                reportRepository = container.reportRepository,
                fileStore = container.fileStore,
                syncRepository = container.syncRepository,
                activeUserId = currentUser?.id ?: "u-operador",
                onReportSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable(Routes.CAPTURE) {
            CaptureScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.REPORT_DETAIL,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType }),
        ) {
            ReportDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.QUEUE) {
            QueueScreen(
                reportRepository = container.reportRepository,
                syncRepository = container.syncRepository,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
