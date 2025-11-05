package com.lerchenflo.hallenmanager_server.database.repository

import com.lerchenflo.hallenmanager_server.database.model.CornerPoint
import com.lerchenflo.hallenmanager_server.database.model.Item
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface CornerPointRepository: MongoRepository<CornerPoint, ObjectId> {

    fun findAllByItemIdIn(itemIds: List<String>): List<CornerPoint>

}