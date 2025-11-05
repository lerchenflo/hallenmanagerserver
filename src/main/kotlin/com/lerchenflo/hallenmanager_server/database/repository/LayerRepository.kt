package com.lerchenflo.hallenmanager_server.database.repository

import com.lerchenflo.hallenmanager_server.database.model.Layer
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface LayerRepository: MongoRepository<Layer, ObjectId> {

}