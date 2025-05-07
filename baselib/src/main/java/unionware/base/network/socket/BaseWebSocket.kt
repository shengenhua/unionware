package unionware.base.network.socket

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class BaseWebSocket(private var okHttpClient: OkHttpClient = OkHttpClient()) {
    private var newWebSocket: WebSocket? = null
    val statusFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var messageListener: ((text: String) -> Unit)? = null

    fun connect(url: String) {
        if (newWebSocket != null) {
            close()
        }
        val request = Request.Builder().url(url).build()
        newWebSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                //连接成功
                statusFlow.onSubscription {
                    emit(true)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                //消息
                messageListener?.invoke(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                //断开
                statusFlow.onSubscription {
                    emit(false)
                }
            }
        })
    }

    fun send(text: String) {
        newWebSocket?.send(text)
    }


    fun close() {
        newWebSocket?.close(1000, "关闭连接")
        newWebSocket = null
    }
}