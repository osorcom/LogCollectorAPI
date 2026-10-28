package cat.inslaferreria.psp.model

import kotlinx.serialization.Serializable

@Serializable
enum class NivellLog {
    INFO, WARNING, ERROR
}

@Serializable
data class LogEntry(
    val id: Long,
    val timestamp: String,
    val nivell: NivellLog,
    val servei: String,
    val missatge: String
)

@Serializable
data class LogRequest(
    val nivell: NivellLog,
    val servei: String,
    val missatge: String
)