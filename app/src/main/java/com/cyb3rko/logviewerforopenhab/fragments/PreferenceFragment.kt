package com.cyb3rko.logviewerforopenhab.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cyb3rko.logviewerforopenhab.*
import com.cyb3rko.logviewerforopenhab.ANALYTICS_COLLECTION
import com.cyb3rko.logviewerforopenhab.CONNECTION_OVERVIEW_ENABLED
import com.cyb3rko.logviewerforopenhab.CRASHLYTICS_COLLECTION
import com.cyb3rko.logviewerforopenhab.DATA_DELETION
import com.cyb3rko.logviewerforopenhab.ORIENTATION
import com.cyb3rko.logviewerforopenhab.SHARED_PREFERENCE
import com.cyb3rko.logviewerforopenhab.getListOfConnections
import com.cyb3rko.logviewerforopenhab.hideConnections
import com.cyb3rko.logviewerforopenhab.showConnections
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*

class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var analyticsCollectionSwitch: SwitchPreferenceCompat
    private lateinit var connectionOverviewSwitch: SwitchPreferenceCompat
    private lateinit var crashlyticsCollectionSwitch: SwitchPreferenceCompat
    private lateinit var hideTopbarSwitch: SwitchPreferenceCompat
    private lateinit var mySPR: SharedPreferences
    private lateinit var nightModeList: ListPreference
    private lateinit var orientationList: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        preferenceManager.sharedPreferencesName = SHARED_PREFERENCE
        mySPR = preferenceManager.sharedPreferences
        orientationList = findPreference(ORIENTATION)!!
        connectionOverviewSwitch = findPreference(CONNECTION_OVERVIEW_ENABLED)!!
        nightModeList = findPreference(NIGHTMODE)!!
        hideTopbarSwitch = findPreference(HIDE_TOPBAR)!!
        analyticsCollectionSwitch = findPreference(ANALYTICS_COLLECTION)!!
        crashlyticsCollectionSwitch = findPreference(CRASHLYTICS_COLLECTION)!!

        orientationList.value = mySPR.getString(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED.toString())
        connectionOverviewSwitch.isChecked = mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)
        nightModeList.value = mySPR.getString(NIGHTMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())
        hideTopbarSwitch.isChecked = mySPR.getBoolean(HIDE_TOPBAR, false)
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
                    hideConnections(requireActivity())
                }
                true
            }
            NIGHTMODE -> {
                nightModeList.setOnPreferenceChangeListener { _, newValue ->
                    val navView = requireActivity().nav_view
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