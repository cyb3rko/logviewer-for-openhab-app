package com.cyb3rko.logviewerforopenhab.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cyb3rko.logviewerforopenhab.*
import com.cyb3rko.logviewerforopenhab.databinding.FragmentMainBinding
import com.google.android.material.button.MaterialButton

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var myContext: Context

    private lateinit var editor: SharedPreferences.Editor
    private lateinit var hostnameIPAddressString: String
    private lateinit var link: String
    private lateinit var mySPR: SharedPreferences
    private var portInt = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        mySPR = myContext.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
        editor = mySPR.edit()
        editor.apply()

        setEditButtonClickListener()
        setConnectButtonClickListener(root)
        setConnectCheckClickListener()

        return root
    }

    override fun onStart() {
        super.onStart()
        statusRestoring()
    }

    private fun statusRestoring() {
        if (!mySPR.getBoolean(HTTPS_ACTIVATED, false)) {
            binding.httpToggles.check((binding.httpToggles[0] as MaterialButton).id)
        } else {
            binding.httpToggles.check((binding.httpToggles[1] as MaterialButton).id)
        }
        binding.connectCheck.isChecked = mySPR.getBoolean(CONNECT_CHECK, false)

        if (mySPR.getString(HOSTNAME_STRING, "") == "" || mySPR.getString(HOSTNAME_STRING, "") == "0") {
            binding.hostnameIpAddressText.setText("")
            binding.hostnameIpAddress.isEnabled = true
        } else {
            binding.hostnameIpAddressText.setText(mySPR.getString(HOSTNAME_STRING, "0"))
            hostnameIPAddressString = mySPR.getString(HOSTNAME_STRING, "0")!!
            binding.hostnameIpAddress.isEnabled = false
        }

        binding.hostnameIpAddressCheck.isChecked = mySPR.getBoolean(HOSTNAME_CHECK, true)

        if (mySPR.getInt(PORT_INT, 0) == 0) {
            binding.portText.setText("")
            binding.hostnameIpAddress.isEnabled = true
        } else {
            binding.portText.setText(mySPR.getInt(PORT_INT, 0).toString())
            binding.port.isEnabled = false
            portInt = mySPR.getInt(PORT_INT, 9001)
        }

        binding.portCheck.isChecked = mySPR.getBoolean(PORT_CHECK, true)

        val hostname = binding.hostnameIpAddressText.text.toString()
        val port = binding.portText.text.toString()
        if (hostname.isNotEmpty() && port.isNotEmpty()) {
            linkGeneration(hostname, port)
            binding.httpToggles[0].isEnabled = false
            binding.httpToggles[1].isEnabled = false
            binding.hostnameIpAddressCheck.isEnabled = false
            binding.portCheck.isEnabled = false
            binding.connectCheck.isEnabled = true
            binding.connectButton.text = getString(R.string.main_connect_button_2)
            binding.editButton.isEnabled = true
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
        } else {
            "https"
        }

        val portString = if (portInt != -1) ":$portInt" else ""
        link = "$prefix://$hostnameIPAddressString$portString"
        binding.linkView.text = link
    }

    private fun setConnectButtonClickListener(v: View) {
        binding.connectButton.setOnClickListener { view ->
            val tempHostname = binding.hostnameIpAddressText.text.toString().trim()
            val tempPort = binding.portText.text.toString().trim()
            if (tempHostname.isNotEmpty()) {
                binding.hostnameIpAddress.error = ""
                if (binding.linkView.text.toString().isEmpty()) {
                    linkGeneration(tempHostname, tempPort)

                    binding.httpToggles[0].isEnabled = false
                    binding.httpToggles[1].isEnabled = false
                    binding.hostnameIpAddress.isEnabled = false
                    binding.port.isEnabled = false
                    binding.hostnameIpAddressCheck.isEnabled = false
                    binding.portCheck.isEnabled = false
                    binding.connectCheck.isEnabled = true
                    binding.editButton.isEnabled = true

                    val httpMode = (binding.httpToggles[1] as MaterialButton).isChecked
                    editor.putBoolean(HTTPS_ACTIVATED, httpMode)

                    if (binding.hostnameIpAddressCheck.isChecked) {
                        editor.putString(HOSTNAME_STRING, hostnameIPAddressString)
                        editor.putBoolean(HOSTNAME_CHECK, true)
                    } else {
                        editor.putString(HOSTNAME_STRING, "")
                        editor.putBoolean(HOSTNAME_CHECK, false)
                    }

                    if (binding.portCheck.isChecked) {
                        editor.putBoolean(PORT_CHECK, true)

                        if (binding.portText.text.toString().trim().isNotEmpty()) {
                            editor.putInt(PORT_INT, portInt)
                        } else {
                            binding.portText.setText(9001.toString())
                            editor.putInt(PORT_INT, 9001)
                        }
                    } else {
                        binding.portText.setText(9001.toString())
                        editor.putInt(PORT_INT, 0)
                        editor.putBoolean(PORT_CHECK, false)
                    }

                    editor.putString(LINK, link).apply()

                    binding.connectButton.text = getString(R.string.main_connect_button_2)
                } else {
                    if (view != null) {
                        val imm = view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    findNavController().navigate(R.id.nav_webview)

                    if (mySPR.getBoolean(CONNECTION_OVERVIEW_ENABLED, true)) {
                        storeAndShowConnection((binding.httpToggles[1] as MaterialButton).isChecked, tempHostname, tempPort.toInt())
                    }
                }
            } else {
                binding.hostnameIpAddress.error = getString(R.string.error_fill_out)
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
            connections.add(newConnection)
            val newConnections = connections.joinToString(";")
            editor.putString(CONNECTIONS, newConnections).commit()
            showConnections(mySPR, connections, activity)
        }
    }

    private fun setEditButtonClickListener() {
        binding.editButton.setOnClickListener {
            binding.httpToggles[0].isEnabled = true
            binding.httpToggles[1].isEnabled = true
            binding.hostnameIpAddress.isEnabled = true
            binding.hostnameIpAddressCheck.isEnabled = true
            binding.port.isEnabled = true
            binding.portCheck.isEnabled = true
            binding.connectCheck.isEnabled = false
            binding.connectButton.text = getString(R.string.main_connect_button_1)
            binding.editButton.isEnabled = false
            binding.linkView.text = ""
        }
    }

    private fun setConnectCheckClickListener() {
        binding.connectCheck.setOnCheckedChangeListener { _, b ->
            editor.putBoolean(CONNECT_CHECK, b)
            editor.putBoolean(AUTO_START, b).apply()
        }
    }
}