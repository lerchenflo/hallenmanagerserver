package com.lerchenflo.hallenmanager_server.database.repository

import com.lerchenflo.hallenmanager_server.database.model.Item
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ItemRepository: MongoRepository<Item, ObjectId> {

}