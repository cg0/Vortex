package uk.cg0.vortex.config

import uk.cg0.vortex.handler.KeyValueConfigHandler
import java.nio.file.Files
import java.nio.file.Paths

class Config {
    private var configData = HashMap<String, String>()
    private val keyValueConfigHandler = KeyValueConfigHandler()

    fun load(fileLocation: String) {
        val fileData = Files.readAllLines(Paths.get(fileLocation))
        configData = keyValueConfigHandler.handleRead(fileData)
    }

    fun save(fileLocation: String) {
        val fileData = keyValueConfigHandler.handleWrite(configData)
        Files.write(Paths.get(fileLocation), fileData)
    }

    operator fun get(key: String): String? {
        return configData[key]
    }

    operator fun set(key: String, value: String) {
        configData[key] = value
    }
}