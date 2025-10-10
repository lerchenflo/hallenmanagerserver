package com.lerchenflo.hallenmanager_server

import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class HallenmanagerServerApplication

fun main(args: Array<String>) {
	runApplication<HallenmanagerServerApplication>(*args)
}

@Document("areas")
data class area(
    val id: ObjectId = ObjectId(),
)

interface areaRepository : MongoRepository<area, ObjectId> {

}

@RestController("/")
class Main(
    val areaRepository: areaRepository,
){

    @GetMapping()
    fun getBasicWebsite() : String {
        areaRepository.insert(area())

        return "Testtest"
    }
}
