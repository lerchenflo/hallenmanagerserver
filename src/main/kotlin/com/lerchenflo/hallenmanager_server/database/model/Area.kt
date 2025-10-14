package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "areas")
data class Area(
    @Id val id: ObjectId = ObjectId.get(),
    val name: String,
    val description: String,
    val createdAt: Long,
    var lastchangedAt: Long,
    var lastchangedBy: String,
)
