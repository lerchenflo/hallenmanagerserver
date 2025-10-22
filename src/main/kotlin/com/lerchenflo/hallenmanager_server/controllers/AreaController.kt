@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.lerchenflo.hallenmanager_server.database.model.Area
import com.lerchenflo.hallenmanager_server.database.model.AreaAsSyncObject
import com.lerchenflo.hallenmanager_server.database.model.asSyncObject
import com.lerchenflo.hallenmanager_server.database.repository.AreaRepository
import com.lerchenflo.hallenmanager_server.util.computeDiffs
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    fun upsertArea(
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
                    serverId = existingEntry.serverId,
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
                serverId = ObjectId.get(),
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



    data class IdTimeStamp(
        val id: ObjectId,
        val timeStamp: Instant
    )

    @PostMapping
    @RequestMapping("/sync")
    fun areaSync(
        @RequestBody clientList: List<IdTimeStamp>
    ) : List<AreaAsSyncObject> {
        val localList = areaRepository.findAll()
            .map { area ->
                IdTimeStamp(
                    id = area.serverId,
                    timeStamp = area.lastchangedAt
                )
        }

        val resultTimestamps = computeDiffs(clientList, localList).getAll()
        val changedAreaids = resultTimestamps.map {
            it.id
        }

        return areaRepository.findAllById(changedAreaids).map { area ->
            area.asSyncObject()
        }
    }
}