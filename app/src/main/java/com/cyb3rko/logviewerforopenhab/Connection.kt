package com.cyb3rko.logviewerforopenhab

internal data class Connection(
    val httpsActivated: Boolean,
    val hostName: String,
    val port: Int
) {
    override fun toString(): String {
        val http = if (httpsActivated) "https" else "http"
        return "$http://$hostName:$port"
    }

    fun toLink(): String {
        val http = if (httpsActivated) "https" else "http"
        val portString = if (port != -1) ":$port" else ""
        return "$http://$hostName$portString"
    }

    fun toCaption(): String {
        val emojiCode = if (httpsActivated) 0x1F512 else 0x1F513
        val emoji = String(Character.toChars(emojiCode))
        val portString = if (port != -1) ":$port" else ""
        return "$hostName$portString $emoji"
    }

    companion object {
        fun fromString(string: String): Connection {
            val parts1 = string.split("://")
            val parts2 = parts1[1].split(":")
            return Connection(parts1[0] == "https", parts2[0], parts2[1].toInt())
        }
    }
}
