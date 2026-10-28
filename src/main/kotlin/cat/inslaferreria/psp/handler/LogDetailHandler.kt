package cat.inslaferreria.psp.handler

import cat.inslaferreria.psp.repository.LogRepository
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LogDetailHandler : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        try {
            val path = exchange.requestURI.path
            val idStr = path.removePrefix("/api/v1/logs/")
            val id = idStr.toLongOrNull()

            if (id == null) {
                sendResponse(exchange, 400, "{\"error\": \"ID invàlid\"}")
                return
            }

            when (exchange.requestMethod) {
                "GET" -> handleGetLogById(exchange, id)
                else -> sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}")
            }
        } catch (e: Exception) {
            sendResponse(exchange, 500, "{\"error\": \"${e.message}\"}")
        }
    }

    private fun handleGetLogById(exchange: HttpExchange, id: Long) {
        val log = LogRepository.findById(id)
        if (log != null) {
            val responseJson = Json.encodeToString(log)
            sendResponse(exchange, 200, responseJson)
        } else {
            sendResponse(exchange, 404, "{\"error\": \"El log sol·licitat no existeix\"}")
        }
    }

    private fun sendResponse(exchange: HttpExchange, statusCode: Int, response: String) {
        val bytes = response.toByteArray(Charsets.UTF_8)
        exchange.responseHeaders.add("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(statusCode, bytes.size.toLong())
        exchange.responseBody.use { it.write(bytes) }
    }
}