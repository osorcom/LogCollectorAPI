package cat.inslaferreria.psp

import cat.inslaferreria.psp.handler.LogDetailHandler
import cat.inslaferreria.psp.handler.LogsHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.concurrent.Executors

fun main() {
    val port = 8080
    val server = HttpServer.create(InetSocketAddress(port), 0)

    // Associem els contextos directament a les classes HttpHandler
    server.createContext("/api/v1/logs", LogsHandler())
    server.createContext("/api/v1/logs/", LogDetailHandler())

    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    server.start()

    println("Servidor HTTP iniciat a http://localhost:$port/api/v1")
}