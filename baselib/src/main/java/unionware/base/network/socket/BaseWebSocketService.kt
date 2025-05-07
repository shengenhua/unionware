@file:OptIn(DelicateCoroutinesApi::class)

package com.unionware.base.lib_network.socket

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import java.net.InetAddress
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


class BaseWebSocketService() {

    class KeyWebSocket(
        var webSocket: WebSocket? = null,
        var name: String? = null,
    )

    private var mockWebServer: MockWebServer? = MockWebServer()
    private var open = false
    private var users: MutableMap<String, KeyWebSocket> = ConcurrentHashMap<String, KeyWebSocket>()

    var messageListener: ((uuid: String, text: String) -> Unit)? = null
    var statusListener: ((open: Boolean) -> Unit)? = null

    fun open(context: Context, port: Int? = null) {
        if (open) {
            return
        }
        if (mockWebServer == null) {
            mockWebServer = MockWebServer()
        }

        mockWebServer?.dispatcher = object : QueueDispatcher() {

            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path == "/") {
                    //客户端进入
                    val uuid = UUID.randomUUID().toString();
                    return MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            super.onOpen(webSocket, response)
                            users[uuid] = KeyWebSocket(webSocket)
                        }

                        override fun onMessage(webSocket: WebSocket, text: String) {
                            super.onMessage(webSocket, text)
                            //收到新信息
                            messageListener?.invoke(uuid, text)
                        }

                        override fun onClosing(
                            webSocket: WebSocket,
                            code: Int,
                            reason: String,
                        ) {
                            super.onClosing(webSocket, code, reason)
                            users.remove(uuid)
                        }

                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            super.onClosed(webSocket, code, reason)
                            //关闭
                        }

                        override fun onFailure(
                            webSocket: WebSocket,
                            t: Throwable,
                            response: Response?,
                        ) {
                            super.onFailure(webSocket, t, response)
                        }
                    })
                }
                return MockResponse().setResponseCode(200)
            }
        }
        mockWebServer?.start(
            InetAddress.getByName(getLocalIpAddress(context)),
            port ?: 8081
        )
        open = true
        GlobalScope.launch(Dispatchers.Main) {
            statusListener?.invoke(true)
        }
    }

    fun getLocalIpAddress(context: Context): String? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        if (wifiManager.connectionInfo != null) {
            val ipAddress = wifiManager.connectionInfo.ipAddress
            return Formatter.formatIpAddress(ipAddress)
        }
        return InetAddress.getLoopbackAddress().hostName
    }

    fun getWebSocketAddress(): String {
        return "ws://" + mockWebServer?.hostName + ":" + mockWebServer?.port
    }

    fun disConnect() {
        // 停止MockWebServer
        mockWebServer?.shutdown()
        statusListener?.invoke(false)
        mockWebServer = null
        open = false
    }

    fun saveUser(uuid: String, key: String) {
        users[key]?.name = key
    }

    /**
     * 发送数据
     */
    fun send(text: String, key: String? = null): Boolean {
        return if (key != null) {
            users.values.firstOrNull { it.name == key }?.webSocket?.send(text) == true
        } else {
            users.forEach {
                it.value.webSocket?.send(text)
            }
            users.isNotEmpty()
        }
    }
}