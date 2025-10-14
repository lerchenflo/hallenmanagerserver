package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import kotlin.time.Clock

data class Item(
    @Id val itemid: ObjectId = ObjectId.get(),
    val areaId: Long,
    val title: String,
    val description: String,
    val color: Long?,
    val onArea: Boolean,
    val lastchanged: String,
    val created : String,
)
