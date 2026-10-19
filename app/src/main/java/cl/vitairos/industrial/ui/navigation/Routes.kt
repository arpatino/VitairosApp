package cl.vitairos.industrial.ui.navigation

/** Rutas de las pantallas mínimas definidas en el cuestionario (§11). */
object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val NEW_REPORT = "new_report"
    const val CAPTURE = "capture"
    const val REPORT_DETAIL = "report_detail/{reportId}"
    const val QUEUE = "queue"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    fun reportDetail(reportId: String) = "report_detail/$reportId"
}
