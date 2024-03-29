package com.cyb3rko.logviewerforopenhab.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.cyb3rko.logviewerforopenhab.*
import com.cyb3rko.logviewerforopenhab.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

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
        mySPR = preferenceManager.sharedPreferences!!
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

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
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
                        MaterialAlertDialogBuilder(myContext)
                            .setTitle(R.string.settings_manage_connections_dialog_title)
                            .setMessage(R.string.settings_manage_connections_dialog1_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                        return true
                    }
                    val connectionLinks = mutableListOf<String>()
                    connectionList.forEach { connectionLinks.add(it.toLink()) }

                    val checkedItems = BooleanArray(connectionLinks.size) { false }

                    MaterialAlertDialogBuilder(myContext)
                        .setTitle(R.string.settings_manage_connections_dialog_title)
                        .setMultiChoiceItems(
                            connectionLinks.toTypedArray(),
                            checkedItems
                        ) { _, index, checked ->
                            checkedItems[index] = checked
                        }
                        .setPositiveButton(R.string.settings_manage_connections_dialog2_button) { _, _ ->
                            checkedItems.forEachIndexed { index, b ->
                                if (b) connectionList.removeAt(index)
                                val newConnections = connectionList.joinToString(";")
                                mySPR.edit().putString(CONNECTIONS, newConnections).apply()
                                showConnections(mySPR, connectionList, myContext as Activity)
                            }
                        }
                        .show()
                    true
                } else {
                    showToast(getString(R.string.settings_manage_connections_error))
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
                FirebaseAnalytics.getInstance(requireContext())
                    .setAnalyticsCollectionEnabled(analyticsCollectionSwitch.isChecked)
                true
            }
            CRASHLYTICS_COLLECTION -> {
                FirebaseCrashlytics.getInstance()
                    .setCrashlyticsCollectionEnabled(crashlyticsCollectionSwitch.isChecked)
                true
            }
            DATA_DELETION -> {
                FirebaseAnalytics.getInstance(requireActivity()).resetAnalyticsData()
                FirebaseCrashlytics.getInstance().deleteUnsentReports()
                showToast(getString(R.string.settings_data_deletion_done))
                true
            }
            else -> false
        }
    }
}
