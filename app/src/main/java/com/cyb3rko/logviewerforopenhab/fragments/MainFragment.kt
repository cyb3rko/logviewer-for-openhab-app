package com.cyb3rko.logviewerforopenhab.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cyb3rko.logviewerforopenhab.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainFragment : Fragment() {

    private lateinit var connectButton: Button
    private lateinit var connectCheck: CheckBox
    private lateinit var editButton: Button
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var hostnameIPAddress: TextInputLayout
    private lateinit var hostnameIPAddressCheck: CheckBox
    private lateinit var hostnameIPAddressString: String
    private lateinit var hostnameIPAddressText: TextInputEditText
    private lateinit var httpToggles: MaterialButtonToggleGroup
    private lateinit var link: String
    private lateinit var linkView: TextView
    private lateinit var mySPR: SharedPreferences
    private lateinit var port: TextInputLayout
    private lateinit var portCheck: CheckBox
    private var portInt = 0
    private lateinit var portText: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        connectButton = v.findViewById(R.id.connect_button)
        connectCheck = v.findViewById(R.id.connect_check)
        editButton = v.findViewById(R.id.edit_button)
        hostnameIPAddressCheck = v.findViewById(R.id.hostname_ip_address_check)
        portCheck = v.findViewById(R.id.port_check)
        hostnameIPAddress = v.findViewById(R.id.hostname_ip_address)
        httpToggles = v.findViewById(R.id.http_toggles)
        port = v.findViewById(R.id.port)
        hostnameIPAddressText = v.findViewById(R.id.hostname_ip_address_text)
        portText = v.findViewById(R.id.port_text)
        linkView = v.findViewById(R.id.link_view)

        mySPR = v.context.getSharedPreferences(SHARED_PREFERENCE, 0)
        editor = mySPR.edit()
        editor.apply()

        setEditButtonClickListener()
        setConnectButtonClickListener(v)
        setConnectCheckClickListener()

        return v
    }

    override fun onStart() {
        super.onStart()
        statusRestoring()
    }

    private fun statusRestoring() {
        if (!mySPR.getBoolean(HTTPS_ACTIVATED, false)) {
            httpToggles.check((httpToggles[0] as MaterialButton).id)
        } else {
            httpToggles.check((httpToggles[1] as MaterialButton).id)
        }
        connectCheck.isChecked = mySPR.getBoolean(CONNECT_CHECK, false)

        if (mySPR.getString(HOSTNAME_STRING, "") == "" || mySPR.getString(HOSTNAME_STRING, "") == "0") {
            hostnameIPAddressText.setText("")
            hostnameIPAddress.isEnabled = true
        } else {
            hostnameIPAddressText.setText(mySPR.getString(HOSTNAME_STRING, "0"))
            hostnameIPAddressString = mySPR.getString(HOSTNAME_STRING, "0")!!
            hostnameIPAddress.isEnabled = false
        }

        hostnameIPAddressCheck.isChecked = mySPR.getBoolean(HOSTNAME_CHECK, true)

        if (mySPR.getInt(PORT_INT, 0) == 0) {
            portText.setText("")
            hostnameIPAddress.isEnabled = true
        } else {
            portText.setText(mySPR.getInt(PORT_INT, 0).toString())
            port.isEnabled = false
            portInt = mySPR.getInt(PORT_INT, 9001)
        }

        portCheck.isChecked = mySPR.getBoolean(PORT_CHECK, true)

        val hostname = hostnameIPAddressText.text.toString()
        val port = portText.text.toString()
        if (hostname.isNotEmpty() && port.isNotEmpty()) {
            linkGeneration(hostname, port)
            httpToggles[0].isEnabled = false
            httpToggles[1].isEnabled = false
            hostnameIPAddressCheck.isEnabled = false
            portCheck.isEnabled = false
            connectCheck.isEnabled = true
            connectButton.text = getString(R.string.main_connect_button_2)
            editButton.isEnabled = true
        }
    }

    private fun linkGeneration(hostname: String, port: String) {
        hostnameIPAddressString = hostname
        portInt = if (port.isNotEmpty()) {
            port.toInt()
        } else {
            9001
        }
        val prefix = if (requireView().findViewById<MaterialButton>(R.id.http_button).isChecked) {
            "http"
        } else "https"
        link = "$prefix://$hostnameIPAddressString:$portInt"
        linkView.text = link
    }

    private fun setConnectButtonClickListener(v: View) {
        connectButton.setOnClickListener { view ->
            val tempHostname = hostnameIPAddressText.text.toString().trim()
            val tempPort = portText.text.toString().trim()
            if (tempHostname.isNotEmpty()) {
                hostnameIPAddress.error = ""
                if (linkView.text.toString().isEmpty()) {
                    linkGeneration(tempHostname, tempPort)

                    httpToggles[0].isEnabled = false
                    httpToggles[1].isEnabled = false
                    hostnameIPAddress.isEnabled = false
                    port.isEnabled = false
                    hostnameIPAddressCheck.isEnabled = false
                    portCheck.isEnabled = false
                    connectCheck.isEnabled = true
                    editButton.isEnabled = true

                    val httpMode = (httpToggles[1] as MaterialButton).isChecked
                    editor.putBoolean(HTTPS_ACTIVATED, httpMode)

                    if (hostnameIPAddressCheck.isChecked) {
                        editor.putString(HOSTNAME_STRING, hostnameIPAddressString)
                        editor.putBoolean(HOSTNAME_CHECK, true)
                    } else {
                        editor.putString(HOSTNAME_STRING, "")
                        editor.putBoolean(HOSTNAME_CHECK, false)
                    }

                    if (portCheck.isChecked) {
                        editor.putBoolean(PORT_CHECK, true)

                        if (portText.text.toString().trim().isNotEmpty()) {
                            editor.putInt(PORT_INT, portInt)
                        } else {
                            portText.setText(9001.toString())
                            editor.putInt(PORT_INT, 9001)
                        }
                    } else {
                        portText.setText(9001.toString())
                        editor.putInt(PORT_INT, 0)
                        editor.putBoolean(PORT_CHECK, false)
                    }

                    editor.putString(LINK, link).apply()

                    connectButton.text = getString(R.string.main_connect_button_2)
                } else {
                    if (view != null) {
                        val imm = view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    findNavController().navigate(R.id.nav_webview)

                    if (mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)) {
                        storeAndShowConnection((httpToggles[1] as MaterialButton).isChecked, tempHostname, tempPort.toInt())
                    }
                }
            } else {
                hostnameIPAddress.error = getString(R.string.error_fill_out)
            }
        }
    }

    private fun storeAndShowConnection(httpsActivated: Boolean, hostname: String, port: Int) {
        val newConnection = Connection(httpsActivated, hostname, port)
        val connections = getListOfConnections(mySPR)
        if (!connections.contains(newConnection)) {
            if (connections.size >= 3) {
                connections.removeAt(0)
            }
            var newConnections = ""
            var http: String
            connections.forEach {
                http = if (it.httpsActivated) "https" else "http"
                newConnections += "$http://${it.hostName}:${it.port};"
            }
            http = if (httpsActivated) "https" else "http"
            newConnections += "$http://${hostname}:${portInt}"
            editor.putString(CONNECTIONS, newConnections).commit()
            connections.add(newConnection)
            showConnections(mySPR, connections, activity)
        }
    }

    private fun setEditButtonClickListener() {
        editButton.setOnClickListener {
            httpToggles[0].isEnabled = true
            httpToggles[1].isEnabled = true
            hostnameIPAddress.isEnabled = true
            hostnameIPAddressCheck.isEnabled = true
            port.isEnabled = true
            portCheck.isEnabled = true
            connectCheck.isEnabled = false
            connectButton.text = getString(R.string.main_connect_button_1)
            editButton.isEnabled = false
            linkView.text = ""
        }
    }

    private fun setConnectCheckClickListener() {
        connectCheck.setOnCheckedChangeListener { _, b ->
            editor.putBoolean(CONNECT_CHECK, b)
            editor.putBoolean(AUTO_START, b).apply()
        }
    }
}