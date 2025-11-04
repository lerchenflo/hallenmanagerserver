@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.database.model

import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class IdTimeStamp(
    val id: ObjectId,
    val timeStamp: String
)