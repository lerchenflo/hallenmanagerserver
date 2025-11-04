@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.util

import com.lerchenflo.hallenmanager_server.database.model.IdTimeStamp
import kotlin.time.ExperimentalTime

data class SyncResult(
    val onlyOnServer: List<IdTimeStamp>,
    val serverNewer: List<IdTimeStamp>
){
    fun getAll() : List<IdTimeStamp>{
        return onlyOnServer + serverNewer
    }
}

fun computeDiffs(clientList: List<IdTimeStamp>, serverList: List<IdTimeStamp>): SyncResult {
    val clientMap = clientList.associateBy { it.id }
    val serverMap = serverList.associateBy { it.id }

    val onlyOnServer = mutableListOf<IdTimeStamp>()
    val serverNewer = mutableListOf<IdTimeStamp>()

    // union of all ids (we only act on server-only or server-newer cases)
    val allIds = clientMap.keys + serverMap.keys
    for (id in allIds) {
        val c = clientMap[id]
        val s = serverMap[id]

        when {
            c == null && s != null -> {
                onlyOnServer += s
            }
            c != null && s != null -> {
                // both exist -> only care if server is newer
                if (s.timeStamp > c.timeStamp) {
                    serverNewer += s
                }
            }
        }
    }

    return SyncResult(
        onlyOnServer = onlyOnServer,
        serverNewer = serverNewer
    )
}
