package com.pettrack.app.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Bottom-nav tabs
    const val COMMUNITY = "community"
    const val MY_REPORTS = "my_reports"
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"

    // Full-screen (no bottom bar)
    const val REPORT_PET = "report_pet?petId={petId}"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val NOTIFICATIONS = "notifications"

    /** Build a report_pet route; pass an id to open in edit mode. */
    fun reportPet(petId: String? = null): String =
        if (petId == null) "report_pet" else "report_pet?petId=$petId"

    fun petDetail(petId: String): String = "pet_detail/$petId"
}
