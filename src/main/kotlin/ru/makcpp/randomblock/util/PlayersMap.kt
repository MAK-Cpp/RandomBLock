package ru.makcpp.randomblock.util

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayersMap<T>(private val map: ConcurrentHashMap<UUID, T> = ConcurrentHashMap()) : MutableMap<UUID, T> by map {
    private fun notNull(playerUUID: UUID, func: PlayersMap<T>.(UUID) -> T?): T = requireNotNull(func(playerUUID)) {
        "Player $playerUUID not found"
    }

    override operator fun get(key: UUID): T = notNull(key) { map[it] }

    override fun remove(key: UUID): T = notNull(key) { map.remove(it) }
}
