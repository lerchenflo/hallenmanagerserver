@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Document(collection = "areas")
data class Area(
    @Id val id: ObjectId = ObjectId.get(),
    val name: String,
    val description: String,
    val createdAt: String,
    var lastchangedAt: String,
    var lastchangedBy: String,
)

data class AreaAsSyncObject(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    var lastchangedAt: String,
    var lastchangedBy: String,
)

fun Area.asSyncObject() : AreaAsSyncObject {
    return AreaAsSyncObject(
        id = id.toHexString(),
        name = name,
        description = description,
        createdAt = createdAt,
        lastchangedAt = lastchangedAt,
        lastchangedBy = lastchangedBy,
    )
}
