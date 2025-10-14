package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class CornerPoint(
    @Id val id: ObjectId = ObjectId.get(),
    val itemId: Long,
    val offsetX: Float,
    val offsetY: Float
)