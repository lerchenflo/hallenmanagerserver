package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class CornerPoint(
    @Id val id: ObjectId? = ObjectId.get(),
    var itemId: String,
    val offsetX: Float,
    val offsetY: Float
)

data class CornerPointAsSyncObject(
    val id: String,
    var itemId: String,
    val offsetX: Float,
    val offsetY: Float
)

fun CornerPoint.toCornerPointAsSyncObject(): CornerPointAsSyncObject {
    return CornerPointAsSyncObject(
        id = id?.toHexString() ?: "",
        itemId = itemId,
        offsetX = offsetX,
        offsetY = offsetY
    )
}