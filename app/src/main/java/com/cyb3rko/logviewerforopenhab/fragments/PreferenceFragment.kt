package com.cyb3rko.logviewerforopenhab.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.isItemChecked
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.cyb3rko.logviewerforopenhab.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.dmoral.toasty.Toasty

class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var myContext: Context

    private lateinit var analyticsCollectionSwitch: SwitchPreferenceCompat
    private lateinit var connectionOverviewSwitch: SwitchPreferenceCompat
    private lateinit var crashlyticsCollectionSwitch: SwitchPreferenceCompat
    private lateinit var hideTopbarSwitch: SwitchPreferenceCompat
    private lateinit var mySPR: SharedPreferences
    private lateinit var nightModeList: ListPreference
    private lateinit var orientationList: ListPreference
    private lateinit var openhabVersionList: ListPreference
    private lateinit var stayAwakeSwitch: SwitchPreferenceCompat

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        preferenceManager.sharedPreferencesName = SHARED_PREFERENCE
        mySPR = preferenceManager.sharedPreferences
        openhabVersionList = findPreference(OPENHAB_VERSION)!!
        hideTopbarSwitch = findPreference(HIDE_TOPBAR)!!
        orientationList = findPreference(ORIENTATION)!!
        stayAwakeSwitch = findPreference(STAY_AWAKE)!!
        connectionOverviewSwitch = findPreference(CONNECTION_OVERVIEW_ENABLED)!!
        nightModeList = findPreference(NIGHTMODE)!!
        analyticsCollectionSwitch = findPreference(ANALYTICS_COLLECTION)!!
        crashlyticsCollectionSwitch = findPreference(CRASHLYTICS_COLLECTION)!!

        openhabVersionList.value = mySPR.getString(OPENHAB_VERSION, "3")
        hideTopbarSwitch.isChecked = mySPR.getBoolean(HIDE_TOPBAR, false)
        orientationList.value = mySPR.getString(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED.toString())
        connectionOverviewSwitch.isChecked = mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)
        stayAwakeSwitch.isChecked = mySPR.getBoolean(STAY_AWAKE, true)
        nightModeList.value = mySPR.getString(NIGHTMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())
        analyticsCollectionSwitch.isChecked = mySPR.getBoolean(ANALYTICS_COLLECTION, true)
        crashlyticsCollectionSwitch.isChecked = mySPR.getBoolean(CRASHLYTICS_COLLECTION, true)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            ORIENTATION -> {
                val myActivity = activity
                if (myActivity != null) {
                    orientationList.setOnPreferenceChangeListener { _, newValue ->
                        myActivity.requestedOrientation = newValue.toString().toInt()
                        myActivity.finish()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        true
                    }
                }
                true
            }
            CONNECTION_OVERVIEW_ENABLED -> {
                if (connectionOverviewSwitch.isChecked) {
                    showConnections(mySPR, getListOfConnections(mySPR), activity)
                } else {
                    hideConnections(activity)
                }
                true
            }
            CONNECTIONS_MANAGE -> {
                if (activity != null) {
                    val connectionList = getListOfConnections(
                        requireActivity().getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE))
                    if (connectionList.isEmpty()) {
                        MaterialDialog(myContext).show {
                            title(R.string.settings_manage_connections_dialog_title)
                            message(R.string.settings_manage_connections_dialog1_message)
                            positiveButton(android.R.string.ok)
                        }
                        return true
                    }
                    val connectionLinks = mutableListOf<String>()
                    connectionList.forEach { connectionLinks.add(it.toLink()) }
                    MaterialDialog(myContext).show {
                        title(R.string.settings_manage_connections_dialog_title)
                        listItemsMultiChoice(items = connectionLinks.toList())
                        positiveButton(R.string.settings_manage_connections_dialog2_button) {
                            val selection = mutableListOf<Int>()
                            repeat(connectionList.size) { i ->
                                if (it.isItemChecked(i)) selection.add(i)
                            }
                            selection.reversed().forEach { i ->
                                connectionList.removeAt(i)
                            }
                            val newConnections = connectionList.joinToString(";")
                            mySPR.edit().putString(CONNECTIONS, newConnections).apply()
                            showConnections(mySPR, connectionList, myContext as Activity)
                        }
                    }
                    true
                } else {
                    Toast.makeText(myContext, R.string.settings_manage_connections_error, Toast.LENGTH_SHORT).show()
                    false
                }
            }
            NIGHTMODE -> {
                nightModeList.setOnPreferenceChangeListener { _, newValue ->
                    val navView = (myContext as MainActivity).navView
                    navView.setCheckedItem(navView.checkedItem?.itemId?.minus(1)!!)
                    AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
                    true
                }
                true
            }
            ANALYTICS_COLLECTION -> {
                FirebaseAnalytics.getInstance(requireContext()).setAnalyticsCollectionEnabled(analyticsCollectionSwitch.isChecked)
                true
            }
            CRASHLYTICS_COLLECTION -> {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crashlyticsCollectionSwitch.isChecked)
                true
            }
            DATA_DELETION -> {
                FirebaseAnalytics.getInstance(requireActivity()).resetAnalyticsData()
                FirebaseCrashlytics.getInstance().deleteUnsentReports()
                Toasty.success(requireContext(), getString(R.string.settings_data_deletion_done), Toasty.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }
}