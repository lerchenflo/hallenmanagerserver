@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Document(collection = "areas")
data class Area(
    @Id val serverId: ObjectId = ObjectId.get(),
    val name: String,
    val description: String,
    val createdAt: Instant,
    var lastchangedAt: Instant,
    var lastchangedBy: String,
)

data class AreaAsSyncObject(
    val serverId: String,
    val name: String,
    val description: String,
    val createdAt: Instant,
    var lastchangedAt: Instant,
    var lastchangedBy: String,
)

fun Area.asSyncObject() : AreaAsSyncObject {
    return AreaAsSyncObject(
        serverId = serverId.toHexString(),
        name = name,
        description = description,
        createdAt = createdAt,
        lastchangedAt = lastchangedAt,
        lastchangedBy = lastchangedBy,
    )
}
