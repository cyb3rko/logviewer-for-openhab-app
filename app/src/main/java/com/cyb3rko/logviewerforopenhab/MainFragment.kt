package com.cyb3rko.logviewerforopenhab

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import es.dmoral.toasty.Toasty

class MainFragment : Fragment() {

    private lateinit var connectButton: Button
    private lateinit var connectCheck: CheckBox
    private lateinit var editButton: Button
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var hostnameIPAddress: TextInputLayout
    private lateinit var hostnameIPAddressCheck: CheckBox
    private lateinit var hostnameIPAddressString: String
    private lateinit var hostnameIPAddressText: TextInputEditText
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
        port = v.findViewById(R.id.port)
        hostnameIPAddressText = v.findViewById(R.id.hostname_ip_address_text)
        portText = v.findViewById(R.id.port_text)
        linkView = v.findViewById(R.id.link_view)

        // load save file and its editor
        mySPR = v.context.getSharedPreferences(SHARED_PREFERENCE, 0)
        editor = mySPR.edit()
        editor.apply()

        // restore last status
        statusRestoring()

        // set onclick listeners
        setEditButtonClickListener()
        setConnectButtonClickListener(v)
        setConnectCheckClickListener()

        // show view
        return v
    }

    // restore last status
    private fun statusRestoring() {
        connectCheck.isChecked = mySPR.getBoolean(CONNECT_CHECK, false)

        // restore textbox status
        if (mySPR.getString(HOSTNAME_STRING, "") == "" || mySPR.getString(HOSTNAME_STRING, "") == "0") {
            hostnameIPAddressText.setText("")
            hostnameIPAddress.isEnabled = true
        } else {
            hostnameIPAddressText.setText(mySPR.getString(HOSTNAME_STRING, "0"))
            hostnameIPAddressString = mySPR.getString(HOSTNAME_STRING, "0")!!
            hostnameIPAddress.isEnabled = false
        }

        // restore checkbox status
        hostnameIPAddressCheck.isChecked = mySPR.getBoolean(HOSTNAME_CHECK, true)

        // restore textbox status
        if (mySPR.getInt(PORT_INT, 0) == 0) {
            portText.setText("")
            hostnameIPAddress.isEnabled = true
        } else {
            portText.setText(mySPR.getInt(PORT_INT, 0).toString())
            port.isEnabled = false
            portInt = mySPR.getInt(PORT_INT, 9001)
        }

        // restore checkbox status
        portCheck.isChecked = mySPR.getBoolean(PORT_CHECK, true)

        val hostname = hostnameIPAddressText.text.toString()
        val port = portText.text.toString()
        if (hostname.isNotEmpty() && port.isNotEmpty()) {
            linkGeneration(hostname, port)
            hostnameIPAddressCheck.isEnabled = false
            portCheck.isEnabled = false
            connectCheck.isEnabled = true
            connectButton.text = getString(R.string.main_connect_button2)
            editButton.isEnabled = true
        }
    }

    // generate and show new link according to user inputs
    private fun linkGeneration(hostname: String, port: String) {
        hostnameIPAddressString = hostname
        portInt = if (port.isNotEmpty()) {
            port.toInt()
        } else {
            9001
        }
        link = "http://$hostnameIPAddressString:$portInt"
        linkView.text = link
    }

    // onClickListener for connect button
    private fun setConnectButtonClickListener(v: View) {
        connectButton.setOnClickListener { view ->
            // check if user entered hostname
            val tempHostname = hostnameIPAddressText.text.toString().trim()
            val tempPort = portText.text.toString().trim()
            if (tempHostname.isNotEmpty()) {
                // if connect button was not clicked before
                hostnameIPAddress.error = ""
                if (linkView.text.toString().isEmpty()) {
                    // generate new link
                    linkGeneration(tempHostname, tempPort)

                    // switch all elements
                    hostnameIPAddress.isEnabled = false
                    port.isEnabled = false
                    hostnameIPAddressCheck.isEnabled = false
                    portCheck.isEnabled = false
                    connectCheck.isEnabled = true
                    editButton.isEnabled = true

                    // store values if user wants to
                    if (hostnameIPAddressCheck.isChecked) {
                        editor.putString(HOSTNAME_STRING, hostnameIPAddressString)
                        editor.putBoolean(HOSTNAME_CHECK, true)
                    } else {
                        editor.putString(HOSTNAME_STRING, "")
                        editor.putBoolean(HOSTNAME_CHECK, false)
                    }

                    // store values if user wants to
                    if (portCheck.isChecked) {
                        editor.putBoolean(PORT_CHECK, true)

                        // check if user entered port
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

                    // store link
                    editor.putString(LINK, link).apply()

                    // change button text
                    connectButton.text = getString(R.string.main_connect_button2)
                } else {
                    if (view != null) {
                        val imm = view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    Toasty.info(v.context, getString(R.string.connecting), Toasty.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.nav_webview)

                    if (mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)) {
                        storeAndShowConnection(tempHostname, tempPort.toInt())
                    }
                }
            } else {
                // show error if one field or both fields are empty
                hostnameIPAddress.error = getString(R.string.error_fill_out)
            }
        }
    }

    private fun storeAndShowConnection(hostname: String, port: Int) {
        val newConnection = Connection(hostname, port)
        val connections = getListOfConnections(mySPR)
        if (!connections.contains(newConnection)) {
            if (connections.size >= 3) {
                connections.removeAt(0)
            }
            var newConnections = ""
            connections.forEach {
                newConnections += "${it.hostName}:${it.port};"
            }
            newConnections += "${hostname}:${portInt}"
            editor.putString(CONNECTIONS, newConnections).commit()
            connections.add(newConnection)
            showConnections(mySPR, connections, activity)
        }
    }

    // onClickListener for both edit buttons
    private fun setEditButtonClickListener() {
        editButton.setOnClickListener {
            hostnameIPAddress.isEnabled = true
            hostnameIPAddressCheck.isEnabled = true
            port.isEnabled = true
            portCheck.isEnabled = true
            connectCheck.isEnabled = false
            connectButton.text = getString(R.string.main_connect_button2)
            editButton.isEnabled = false
            linkView.text = ""
        }
    }

    // onClickListener for connect checkbox
    private fun setConnectCheckClickListener() {
        connectCheck.setOnCheckedChangeListener { _, b -> // store values
            editor.putBoolean(CONNECT_CHECK, b)
            editor.putBoolean(AUTO_START, b).apply()
        }
    }
}