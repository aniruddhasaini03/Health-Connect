package com.healthconnect.app.navigation

sealed class Screen(val route:String){
 data object Splash:Screen("splash")
 data object Login:Screen("login")
 data object Register:Screen("register")
 data object Home:Screen("home")
 data object Reports:Screen("reports")
 data object Ai:Screen("ai")
 data object MedicineReminders:Screen("medicine_reminders")
 data object Profile:Screen("profile")
 data object EditProfile:Screen("edit_profile")
 data object Goals:Screen("goals")
 data object Timeline:Screen("timeline")
 data object Insights:Screen("insights")
 data object HealthData:Screen("health_data")
 data object Hospitals:Screen("hospitals")
 data object Emergency:Screen("emergency")
}
