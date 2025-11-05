@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Document(collection = "items")
@OptIn(ExperimentalTime::class)
data class Item(
    @Id val itemid: ObjectId = ObjectId.get(),
    val areaId: String,
    val title: String,
    val description: String,
    val color: Long?,
    val layers: List<String>,
    val onArea: Boolean,
    var createdAt: String,
    var lastchangedAt: String,
    var lastchangedBy: String,
)

data class ItemAsSyncObject(
    val itemid: String,
    val areaId: String,
    val title: String,
    val description: String,
    val color: Long?,
    val layers: List<String>,
    val onArea: Boolean,
    var createdAt: String,
    var lastchangedAt: String,
    var lastchangedBy: String,
)

fun Item.asSyncObject() : ItemAsSyncObject {
    return ItemAsSyncObject(
        itemid = itemid.toHexString(),
        areaId = areaId,
        title = title,
        description = description,
        color = color,
        layers = layers,
        onArea = onArea,
        createdAt = createdAt,
        lastchangedAt = lastchangedAt,
        lastchangedBy = lastchangedBy,
    )
}