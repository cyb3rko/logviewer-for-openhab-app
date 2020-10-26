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
    private lateinit var hostnameIPAddressCheck: CheckBox
    private lateinit var portCheck: CheckBox
    private lateinit var hostnameIPAddress: TextInputLayout
    private lateinit var hostnameIPAddressText: TextInputEditText
    private lateinit var port: TextInputLayout
    private lateinit var portText: TextInputEditText
    private lateinit var hostnameIPAddressEdit: ImageButton
    private lateinit var portEdit: ImageButton
    private lateinit var mySPR: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var hostnameIPAddressString: String
    private lateinit var link: String
    private lateinit var linkView: TextView
    private var portInt = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        connectButton = v.findViewById(R.id.connect_button)
        connectCheck = v.findViewById(R.id.connect_check)
        hostnameIPAddressCheck = v.findViewById(R.id.hostname_ip_address_check)
        portCheck = v.findViewById(R.id.port_check)
        hostnameIPAddress = v.findViewById(R.id.hostname_ip_address)
        port = v.findViewById(R.id.port)
        hostnameIPAddressText = v.findViewById(R.id.hostname_ip_address_text)
        portText = v.findViewById(R.id.port_text)
        hostnameIPAddressEdit = v.findViewById(R.id.hostname_ip_address_edit)
        portEdit = v.findViewById(R.id.port_edit)
        linkView = v.findViewById(R.id.link_view)

        // load save file and its editor
        mySPR = v.context.getSharedPreferences(SHARED_PREFERENCE, 0)
        editor = mySPR.edit()
        editor.apply()

        // restore last status
        statusRestoring()

        // set onclick listeners
        setConnectButtonClickListener(v)
        setEditButtonClickListener(hostnameIPAddressEdit, hostnameIPAddress, portEdit, hostnameIPAddressCheck)
        setEditButtonClickListener(portEdit, port, hostnameIPAddressEdit, portCheck)
        setConnectCheckClickListener()

        // show view
        return v
    }

    // restore last status
    private fun statusRestoring() {
        // restore chechbox status
        connectCheck.isChecked = mySPR.getBoolean("connectCheck", false)

        // restore textbox status
        if (mySPR.getString("hostnameIPAddressString", "") == "" || mySPR.getString("hostnameIPAddressString", "") == "0") {
            hostnameIPAddressText.setText("")
            hostnameIPAddress.isEnabled = true
        } else {
            hostnameIPAddressText.setText(mySPR.getString("hostnameIPAddressString", "0"))
            hostnameIPAddressString = mySPR.getString("hostnameIPAddressString", "0")!!
            hostnameIPAddress.isEnabled = false
        }

        // restore checkbox status
        hostnameIPAddressCheck.isChecked = mySPR.getBoolean("hostnameIPAddressCheck", true)

        // restore textbox status
        if (mySPR.getInt("portInt", 0) == 0) {
            portText.setText("")
            hostnameIPAddress.isEnabled = true
        } else {
            portText.setText(mySPR.getInt("portInt", 0).toString())
            port.isEnabled = false
            portInt = mySPR.getInt("portInt", 9001)
        }

        // restore checkbox status
        portCheck.isChecked = mySPR.getBoolean("portCheck", true)

        // check if connect was clicked and restore last status
        if (hostnameIPAddressText.text.toString().isNotEmpty() && portText.text.toString().isNotEmpty()) {
            linkGeneration()
            hostnameIPAddressEdit.visibility = View.VISIBLE
            portEdit.visibility = View.VISIBLE
            hostnameIPAddressCheck.visibility = View.INVISIBLE
            portCheck.visibility = View.INVISIBLE
            connectCheck.visibility = View.VISIBLE
            connectButton.text = getString(R.string.connect_button_2)
        }
    }

    // generate and show new link according to user inputs
    private fun linkGeneration() {
        hostnameIPAddressString = hostnameIPAddressText.text.toString()
        portInt = if (portText.text.toString().isNotEmpty()) {
            portText.text.toString().toInt()
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
            if (hostnameIPAddressText.text.toString().isNotEmpty()) {
                // if connect button was not clicked before
                if (linkView.text.toString().isEmpty()) {
                    // generate new link
                    linkGeneration()

                    // switch all elements
                    hostnameIPAddress.isEnabled = false
                    hostnameIPAddressEdit.visibility = View.VISIBLE
                    port.isEnabled = false
                    portEdit.visibility = View.VISIBLE
                    hostnameIPAddressCheck.visibility = View.INVISIBLE
                    portCheck.visibility = View.INVISIBLE
                    connectCheck.visibility = View.VISIBLE

                    // store values if user wants to
                    if (hostnameIPAddressCheck.isChecked) {
                        editor.putString("hostnameIPAddressString", hostnameIPAddressString)
                        editor.putBoolean("hostnameIPAddressCheck", true)
                    } else {
                        editor.putString("hostnameIPAddressString", "")
                        editor.putBoolean("hostnameIPAddressCheck", false)
                    }

                    // store values if user wants to
                    if (portCheck.isChecked) {
                        editor.putBoolean("portCheck", true)

                        // check if user entered port
                        if (portText.text.toString().isNotEmpty()) {
                            editor.putInt("portInt", portInt)
                        } else {
                            portText.setText(9001.toString())
                            editor.putInt("portInt", 9001)
                        }
                    } else {
                        editor.putInt("portInt", 0)
                        editor.putBoolean("portCheck", false)
                    }

                    // store link
                    editor.putString("link", link).apply()

                    // change button text
                    connectButton.text = getString(R.string.connect_button_2)
                } else {
                    if (view != null) {
                        val imm = view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    Toasty.info(v.context, getString(R.string.connecting), Toasty.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.nav_webview)

                    if (mySPR.getBoolean("connectionOverviewEnabled", true)) {
                        val hostnameString = hostnameIPAddressText.text.toString().trim()
                        val portInt = portText.text.toString().trim().toInt()
                        storeAndShowConnection(hostnameString, portInt)
                    }
                }
            } else {
                // show error if one field or both fields are empty
                Toasty.error(v.context, getString(R.string.error_fill_out), Toasty.LENGTH_LONG).show()
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
            editor.putString("connections", newConnections).commit()
            connections.add(newConnection)
            showConnections(mySPR, connections, activity)
        }
    }

    // onClickListener for both edit buttons
    private fun setEditButtonClickListener(imageButton: ImageButton?, textView: TextInputLayout, imageButton2: ImageButton?, checkBox: CheckBox?) {
        imageButton?.setOnClickListener {
            textView.isEnabled = true
            imageButton.visibility = View.INVISIBLE
            imageButton2?.visibility = View.INVISIBLE
            checkBox?.visibility = View.VISIBLE
            connectCheck.visibility = View.INVISIBLE
            linkView.text = ""
            connectButton.text = getString(R.string.connect_button_1)
        }
    }

    // onClickListener for connect checkbox
    private fun setConnectCheckClickListener() {
        connectCheck.setOnCheckedChangeListener { _, b -> // store values
            editor.putBoolean("connectCheck", b)
            editor.putBoolean("autoStart", b).apply()
        }
    }
}