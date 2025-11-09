package com.lerchenflo.hallenmanager_server.database.repository

import com.lerchenflo.hallenmanager_server.database.model.Area
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface AreaRepository: MongoRepository<Area, ObjectId> {
    fun findAllByOrderByLastchangedAtDesc(): List<Area>
}