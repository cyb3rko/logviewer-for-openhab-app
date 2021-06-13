package com.cyb3rko.logviewerforopenhab

internal data class Connection(
    val httpsActivated: Boolean,
    val hostName: String,
    val port: Int
)