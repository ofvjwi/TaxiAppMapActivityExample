package com.example.defaultmapactivity.utils

interface WebSocketListener {

    fun onConnect()

    fun onMessage(data: String)

    fun onDisconnect()

    fun onError(error: String)

}
