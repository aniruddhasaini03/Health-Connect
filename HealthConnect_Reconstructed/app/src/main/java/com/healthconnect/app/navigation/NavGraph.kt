package com.healthconnect.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.ui.ai.ChatScreen
import com.healthconnect.app.ui.auth.*
import com.healthconnect.app.ui.emergency.EmergencyScreen
import com.healthconnect.app.ui.health.*
import com.healthconnect.app.ui.home.HomeScreen
import com.healthconnect.app.ui.hospitals.HospitalsScreen
import com.healthconnect.app.ui.profile.*
import com.healthconnect.app.ui.reminders.MedicineRemindersScreen
import com.healthconnect.app.ui.reports.ReportsScreen

private data class BottomItem(val route:String,val label:String,val icon:ImageVector)
private val bottomItems=listOf(
 BottomItem(Screen.Home.route,"Home",Icons.Default.Home),
 BottomItem(Screen.Reports.route,"Reports",Icons.Default.Description),
 BottomItem(Screen.Ai.route,"AI",Icons.Default.SmartToy),
 BottomItem(Screen.MedicineReminders.route,"Meds",Icons.Default.Alarm),
 BottomItem(Screen.Profile.route,"Profile",Icons.Default.Person)
)

@Composable
fun HealthConnectNavGraph(navController:NavHostController=rememberNavController()){
 val backStack by navController.currentBackStackEntryAsState();val route=backStack?.destination?.route
 val showBottom=route in bottomItems.map{it.route}
 Scaffold(bottomBar={if(showBottom)NavigationBar{bottomItems.forEach{item->NavigationBarItem(selected=route==item.route,onClick={navController.navigate(item.route){popUpTo(Screen.Home.route){saveState=true};launchSingleTop=true;restoreState=true}},icon={Icon(item.icon,null)},label={Text(item.label)})}}}){padding->
  NavHost(navController,startDestination=Screen.Splash.route,modifier=Modifier.padding(padding)){
   composable(Screen.Splash.route){SplashScreen({navController.navigate(Screen.Home.route){popUpTo(Screen.Splash.route){inclusive=true}}},{navController.navigate(Screen.Login.route){popUpTo(Screen.Splash.route){inclusive=true}}})}
   composable(Screen.Login.route){LoginScreen({navController.navigate(Screen.Home.route){popUpTo(Screen.Login.route){inclusive=true}}},{navController.navigate(Screen.Register.route)})}
   composable(Screen.Register.route){RegisterScreen({navController.navigate(Screen.Home.route){popUpTo(Screen.Login.route){inclusive=true}}},{navController.popBackStack()})}
   composable(Screen.Home.route){HomeScreen({navController.navigate(Screen.MedicineReminders.route)},{navController.navigate(Screen.Reports.route)},{navController.navigate(Screen.Ai.route)},{navController.navigate(Screen.Hospitals.route)},{navController.navigate(Screen.Emergency.route)},{navController.navigate(Screen.HealthData.route)})}
   composable(Screen.Reports.route){ReportsScreen{navController.popBackStack()}}
   composable(Screen.Ai.route){ChatScreen()}
   composable(Screen.MedicineReminders.route){MedicineRemindersScreen{navController.popBackStack()}}
   composable(Screen.Profile.route){ProfileScreen({navController.navigate(Screen.EditProfile.route)},{navController.navigate(Screen.Goals.route)},{navController.navigate(Screen.Timeline.route)},{navController.navigate(Screen.Insights.route)},{FirebaseModule.auth.signOut();navController.navigate(Screen.Login.route){popUpTo(0)}})}
   composable(Screen.EditProfile.route){EditProfileScreen{navController.popBackStack()}}
   composable(Screen.Goals.route){GoalsScreen{navController.popBackStack()}}
   composable(Screen.Timeline.route){TimelineScreen{navController.popBackStack()}}
   composable(Screen.Insights.route){InsightsScreen{navController.popBackStack()}}
   composable(Screen.HealthData.route){HealthDataScreen{navController.popBackStack()}}
   composable(Screen.Hospitals.route){HospitalsScreen{navController.popBackStack()}}
   composable(Screen.Emergency.route){EmergencyScreen({navController.navigate(Screen.Hospitals.route)},{navController.popBackStack()})}
  }
 }
}
