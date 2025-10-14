@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.lerchenflo.hallenmanager_server.database.model.Area
import com.lerchenflo.hallenmanager_server.database.repository.AreaRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@RestController()
@RequestMapping("/areas")
class AreaController(
    private val areaRepository: AreaRepository
) {

    data class AreaRequest(
        val areaid: String?,
        val name: String,
        val description: String,
    ){
        fun getId(): String{
            return areaid?.toLong()?.toHexString() ?: ""
        }
    }


    @PostMapping
    fun postmapping(
        @RequestParam("username") userName: String,
        @RequestBody body: AreaRequest
    ) : Area {

        val currentInstant = Clock.System.now()
        var area: Area?

        if (body.areaid != null) {
            //Update existing entry
            val existingEntry = areaRepository.findById(ObjectId(body.getId())).orElse(null)

            if (existingEntry != null) {
                area = Area(
                    id = existingEntry.id,
                    name = body.name,
                    description = body.description,
                    createdAt = existingEntry.createdAt,
                    lastchangedAt = currentInstant,
                    lastchangedBy = userName
                )
            }else {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            }
        }else {
            area = Area(
                id = ObjectId.get(),
                name = body.name,
                description = body.description,
                createdAt = currentInstant,
                lastchangedAt = currentInstant,
                lastchangedBy = userName,
            )
        }

        return areaRepository.save(
            area
        )
    }

    @GetMapping
    fun getAreas(): List<Area> {
        return areaRepository.findAll()
    }

}