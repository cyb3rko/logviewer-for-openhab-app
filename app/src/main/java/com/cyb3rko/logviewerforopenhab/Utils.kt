package com.cyb3rko.logviewerforopenhab

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.navigation.NavigationView

internal val PRIVACY_POLICY = "privacy_policy"
internal val SHARED_PREFERENCE = "Safe2"
internal val TERMS_OF_USE = "terms_of_use"

internal fun getListOfConnections(mySPR: SharedPreferences): MutableList<Connection> {
    val resultList = mutableListOf<Connection>()
    val storedConnections = mySPR.getString("connections", "empty")
    if (storedConnections == "empty") {
        return mutableListOf()
    }
    val tempList = storedConnections?.split(";")
    var parts: List<String>
    tempList?.forEach {
        parts = it.split(":")
        resultList.add(Connection(parts[0], parts[1].toInt()))
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
        connectionsMenu.clear()

        var item: MenuItem
        var link: String
        var parts: List<String>
        connections.forEach { connection ->
            item = connectionsMenu.add("${connection.hostName}:${connection.port}")
            item.setIcon(R.drawable._icon_connection)
            item.setOnMenuItemClickListener { menuItem ->
                link = "http://${menuItem.title}"
                parts = menuItem.title.split(":")
                editor.putString("link", link)
                editor.putString("hostnameIPAddressString", parts[0])
                editor.putInt("portInt", parts[1].toInt()).apply()
                navController.navigate(R.id.nav_webview)
                drawer.close()
                true
            }
        }
    }
}

internal fun hideConnections(activity: Activity) {
    val navView = activity.findViewById<NavigationView>(R.id.nav_view)
    val connectionsMenu = navView.menu.findItem(R.id.nav_connections).subMenu
    connectionsMenu.clear()
}

internal fun showLicenseDialog(context: Context?, type: String) {
    MaterialDialog(context!!, BottomSheet()).show {
         @Suppress("DEPRECATION")
         message(0, Html.fromHtml(context.assets.open("$type.html").bufferedReader().use { it.readText() })) {
            messageTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}