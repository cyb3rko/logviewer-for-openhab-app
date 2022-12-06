package com.cyb3rko.logviewerforopenhab

import android.app.Activity
import android.content.*
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.cyb3rko.logviewerforopenhab.modals.PolicyBottomSheet
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

//// Extension functions

// For Context class

internal fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

internal fun Context.storeToClipboard(label: String, text: String) {
    val clip = ClipData.newPlainText(label, text)
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(clip)
}

internal fun Context.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        this.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        this.storeToClipboard("URL", url)
        this.showToast("Opening URL failed, copied URL instead", Toast.LENGTH_LONG)
    }
}

// For Fragment class

internal fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    this.requireContext().showToast(message, length)
}

internal fun Fragment.openUrl(url: String) {
    this.requireContext().openUrl(url)
}

internal fun Fragment.hideKeyboard() {
    val activity = this.requireActivity()
    val imm = activity.getSystemService(
        AppCompatActivity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

//// Other functions

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
    PolicyBottomSheet(type).show(
        (context as FragmentActivity).supportFragmentManager,
        PolicyBottomSheet.TAG
    )
}

fun setToolbarVisibility(activity: Activity?, visibility: Int) {
    activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility = visibility
}
