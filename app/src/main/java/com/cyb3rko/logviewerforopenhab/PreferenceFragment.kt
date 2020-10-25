package com.cyb3rko.logviewerforopenhab

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.dmoral.toasty.Toasty

class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var analyticsCollectionSwitch: SwitchPreferenceCompat
    private lateinit var autoUpdateSwitch: SwitchPreferenceCompat
    private lateinit var connectionOverviewSwitch: SwitchPreferenceCompat
    private lateinit var crashlyticsCollectionSwitch: SwitchPreferenceCompat
    private lateinit var mySPR: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        preferenceManager.sharedPreferencesName = SHARED_PREFERENCE
        mySPR = preferenceManager.sharedPreferences
        connectionOverviewSwitch = findPreference("connectionOverviewEnabled")!!
        autoUpdateSwitch = findPreference("autoUpdate")!!
        analyticsCollectionSwitch = findPreference("analyticsCollection")!!
        crashlyticsCollectionSwitch = findPreference("crashlyticsCollection")!!

        connectionOverviewSwitch.isChecked = mySPR.getBoolean("connectionOverviewEnabled", true)
        autoUpdateSwitch.isChecked = mySPR.getBoolean("autoUpdate", true)
        analyticsCollectionSwitch.isChecked = mySPR.getBoolean("analyticsCollection", false)
        crashlyticsCollectionSwitch.isChecked = mySPR.getBoolean("crashlyticsCollection", false)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            "connectionOverviewEnabled" -> {
                if (connectionOverviewSwitch.isChecked) {
                    showConnections(mySPR, getListOfConnections(mySPR), activity)
                } else {
                    hideConnections(requireActivity())
                }
                true
            }
            "analyticsCollection" -> {
                FirebaseAnalytics.getInstance(requireContext()).setAnalyticsCollectionEnabled(analyticsCollectionSwitch.isChecked)
                true
            }
            "crashlyticsCollection" -> {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crashlyticsCollectionSwitch.isChecked)
                true
            }
            "dataDeletion" -> {
                FirebaseAnalytics.getInstance(requireActivity()).resetAnalyticsData()
                FirebaseCrashlytics.getInstance().deleteUnsentReports()
                Toasty.success(requireContext(), "Deletion done", Toasty.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }
}