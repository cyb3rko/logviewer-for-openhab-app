package com.cyb3rko.logviewerforopenhab

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.navigation.NavigationView

internal const val PRIVACY_POLICY = "privacy_policy"
internal const val TERMS_OF_USE = "terms_of_use"

internal const val SHARED_PREFERENCE = "Safe2"
internal const val ANALYTICS_COLLECTION = "analytics_collection"
internal const val AUTO_START = "auto_start"
internal const val CONSENT_DATE = "consent_date"
internal const val CONSENT_TIME = "consent_time"
internal const val CONNECT_CHECK = "connect_check"
internal const val CONNECTION_OVERVIEW_ENABLED = "connection_overview_enabled"
internal const val CONNECTIONS_MANAGE = "connections_manage"
internal const val CONNECTIONS = "connections"
internal const val CRASHLYTICS_COLLECTION = "crashlytics_collection"
internal const val DATA_DELETION = "data_deletion"
internal const val FIRST_START = "first_start"
internal const val FIRST_START_WEB = "first_start_web"
internal const val HIDE_TOPBAR = "hide_topbar"
internal const val HOSTNAME_CHECK = "hostname_check"
internal const val HOSTNAME_STRING = "hostname_string"
internal const val HTTPS_ACTIVATED = "https_activated"
internal const val LINK = "link"
internal const val NIGHTMODE = "nightmode"
internal const val ORIENTATION = "orientation"
internal const val OPENHAB_VERSION = "openhab_version"
internal const val PORT_CHECK = "port_check"
internal const val PORT_INT = "port_int"
internal const val REVIEW_COUNTER = "review_counter"
internal const val REVIEW_REVISION = "review_revision"
internal const val STAY_AWAKE = "stay_awake"
internal const val TEXTSIZE_AUTO = "textsize_auto"
internal const val TEXTSIZE_LANDSCAPE = "textsize_landscape"
internal const val TEXTSIZE_PORTRAIT = "textsize_PORTRAIT"

internal fun getListOfConnections(mySPR: SharedPreferences): MutableList<Connection> {
    val resultList = mutableListOf<Connection>()
    val storedConnections = mySPR.getString(CONNECTIONS, "empty")
    if (storedConnections == "empty" || storedConnections == "") {
        return mutableListOf()
    }
    storedConnections?.split(";")?.forEach {
        resultList.add(Connection.fromString(it))
    }
    return resultList
}

internal fun showConnections(mySPR: SharedPreferences, connections: MutableList<Connection>, activity: Activity?) {
    if (activity != null) {
        val editor = mySPR.edit()
        val navView = activity.findViewById<NavigationView>(R.id.nav_view)
        val navController = activity.findNavController(R.id.nav_host_fragment)
        val drawer = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val connectionsMenu = navView.menu.findItem(R.id.nav_connections).subMenu
        connectionsMenu?.clear()

        connections.forEach { connection ->
            connectionsMenu?.let {
                val item = connectionsMenu.add(connection.toCaption())
                item.setIcon(R.drawable._ic_connection)
                item.setOnMenuItemClickListener {
                    connection.apply {
                        editor.putBoolean(HTTPS_ACTIVATED, httpsActivated)
                        editor.putString(LINK, toLink())
                        editor.putString(HOSTNAME_STRING, hostName)
                        editor.putInt(PORT_INT, port).apply()
                    }
                    navController.navigate(R.id.nav_webview)
                    drawer.close()
                    true
                }
            }
        }
    }
}

internal fun hideConnections(activity: Activity?) {
    if (activity != null) {
        val navView = activity.findViewById<NavigationView>(R.id.nav_view)
        val connectionsMenu = navView.menu.findItem(R.id.nav_connections).subMenu
        connectionsMenu?.clear()
    }
}

internal fun showLicenseDialog(context: Context?, type: String) {
    MaterialDialog(context!!, BottomSheet()).show {
         @Suppress("DEPRECATION")
         message(0, Html.fromHtml(context.assets.open("$type.html").bufferedReader().use { it.readText() })) {
            messageTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}

fun setToolbarVisibility(activity: Activity?, visibility: Int) {
    activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility = visibility
}