package cat.inslaferreria.psp.handler

import cat.inslaferreria.psp.model.LogRequest
import cat.inslaferreria.psp.model.NivellLog
import cat.inslaferreria.psp.repository.LogRepository
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LogsHandler : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        try {
            when (exchange.requestMethod) {
                "GET" -> handleGetLogs(exchange)
                "POST" -> handlePostLog(exchange)
                "DELETE" -> handleDeleteLogs(exchange)
                else -> sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}")
            }
        } catch (e: Exception) {
            sendResponse(exchange, 500, "{\"error\": \"${e.message}\"}")
        }
    }

    private fun handleGetLogs(exchange: HttpExchange) {
        val query = exchange.requestURI.query
        val params = parseQueryParams(query)
        val nivellStr = params["nivell"]

        val nivellFiltre = nivellStr?.let {
            try { NivellLog.valueOf(it) } catch (e: Exception) { null }
        }

        val logs = LogRepository.findAll(nivellFiltre)
        val responseJson = Json.encodeToString(logs)
        sendResponse(exchange, 200, responseJson)
    }

    private fun handlePostLog(exchange: HttpExchange) {
        val body = exchange.requestBody.bufferedReader().readText()
        val request = try {
            Json.decodeFromString<LogRequest>(body)
        } catch (e: Exception) {
            sendResponse(exchange, 400, "{\"error\": \"Estructura del log no vàlida\"}")
            return
        }

        val createdLog = LogRepository.save(request)
        val responseJson = Json.encodeToString(createdLog)
        sendResponse(exchange, 201, responseJson)
    }

    private fun handleDeleteLogs(exchange: HttpExchange) {
        LogRepository.clear()
        exchange.sendResponseHeaders(204, -1)
        exchange.close()
    }

    private fun parseQueryParams(query: String?): Map<String, String> {
        if (query.isNullOrBlank()) return emptyMap()
        return query.split("&").associate {
            val parts = it.split("=")
            val key = parts.getOrNull(0) ?: ""
            val value = parts.getOrNull(1) ?: ""
            key to value
        }
    }

    private fun sendResponse(exchange: HttpExchange, statusCode: Int, response: String) {
        val bytes = response.toByteArray(Charsets.UTF_8)
        exchange.responseHeaders.add("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(statusCode, bytes.size.toLong())
        exchange.responseBody.use { it.write(bytes) }
    }
}