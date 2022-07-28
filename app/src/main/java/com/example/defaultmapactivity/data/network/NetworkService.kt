package com.example.defaultmapactivity.data.network

import com.example.defaultmapactivity.utils.WebSocket
import com.example.defaultmapactivity.utils.WebSocketListener

class NetworkService {

    fun createWebSocket(webSocketListener: WebSocketListener): WebSocket {
        return WebSocket(webSocketListener)
    }

}
