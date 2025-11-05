package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "layers")

data class Layer(
    @Id val layerid: ObjectId = ObjectId.get(),
    val name: String,
    val sortId: Int,
    val shown: Boolean,
    val color: Long,
    var createdAt: String,
    var lastchangedAt: String,
    var lastchangedBy: String,
)
