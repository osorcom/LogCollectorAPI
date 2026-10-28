package cat.inslaferreria.psp.repository

import cat.inslaferreria.psp.model.LogEntry
import cat.inslaferreria.psp.model.LogRequest
import cat.inslaferreria.psp.model.NivellLog
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object LogRepository {
    private val logs = ConcurrentHashMap<Long, LogEntry>()
    private val idCounter = AtomicLong(500)

    fun findAll(nivellFiltre: NivellLog? = null): List<LogEntry> {
        val allLogs = logs.values.sortedBy { it.id }
        return if (nivellFiltre != null) {
            allLogs.filter { it.nivell == nivellFiltre }
        } else {
            allLogs
        }
    }

    fun findById(id: Long): LogEntry? = logs[id]

    fun save(request: LogRequest): LogEntry {
        val id = idCounter.incrementAndGet()
        val entry = LogEntry(
            id = id,
            timestamp = Instant.now().toString(),
            nivell = request.nivell,
            servei = request.servei,
            missatge = request.missatge
        )
        logs[id] = entry
        return entry
    }

    fun clear() {
        logs.clear()
        idCounter.set(500)
    }
}