@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.lerchenflo.hallenmanager_server.database.model.Area
import com.lerchenflo.hallenmanager_server.database.model.AreaAsSyncObject
import com.lerchenflo.hallenmanager_server.database.model.IdTimeStamp
import com.lerchenflo.hallenmanager_server.database.model.asSyncObject
import com.lerchenflo.hallenmanager_server.database.repository.AreaRepository
import com.lerchenflo.hallenmanager_server.util.computeDiffs
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.collections.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@RestController()
@RequestMapping("/areas")
class AreaController(
    private val areaRepository: AreaRepository
) {

    data class AreaRequest(
        val areaid: String,
        val name: String,
        val description: String,
    )

    data class AreaResponse(
        val id: String,
        val name: String,
        val description: String,
        val createdAt: String,
        var lastchangedAt: String,
        var lastchangedBy: String,
    )

    @PostMapping
    fun upsertArea(
        @RequestParam("username") userName: String,
        @RequestBody body: AreaRequest
    ) : AreaResponse {

        val currentInstant = Clock.System.now().toEpochMilliseconds().toString()
        var area: Area?

        println("Requestbody: $body")

        if (body.areaid.isNotEmpty()) {
            //Update existing entry
            val existingEntry = areaRepository.findById(ObjectId(body.areaid)).orElse(null)

            println("Existingitem: $existingEntry")

            if (existingEntry != null) {
                area = Area(
                    id = existingEntry.id,
                    name = body.name,
                    description = body.description,
                    createdAt = existingEntry.createdAt,
                    lastchangedAt = currentInstant,
                    lastchangedBy = userName
                )
            } else {
                println("An bad request")
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

        val upsertedArea = areaRepository.save(area)

        return AreaResponse(
            id = upsertedArea.id.toString(),
            name = upsertedArea.name,
            description = upsertedArea.description,
            createdAt = upsertedArea.createdAt,
            lastchangedAt = upsertedArea.lastchangedAt,
            lastchangedBy = upsertedArea.lastchangedBy,
        )
    }

    @GetMapping
    fun getAreas(): List<Area> {
        return areaRepository.findAll()
    }





    data class AreaSyncResponse(
        val updated: List<AreaAsSyncObject>,
        val deleted: List<String> // IDs that were deleted
    )

    @PostMapping("/sync")
    fun areaSync(
        @RequestBody clientList: List<IdTimeStamp>
    ): AreaSyncResponse {
        val localList = areaRepository.findAll()
            .map { area ->
                IdTimeStamp(
                    id = area.id,
                    timeStamp = area.lastchangedAt
                )
            }

        val result = computeDiffs(clientList, localList)

        // Items client has but server doesn't = deleted
        val deletedIds = clientList
            .filter { clientItem ->
                localList.none { it.id == clientItem.id }
            }
            .map { it.id.toString() }

        // Items that changed or are new
        val changedAreaIds = result.getAll().map { it.id }
        val updated = areaRepository.findAllById(changedAreaIds)
            .map { it.asSyncObject() }

        return AreaSyncResponse(
            updated = updated,
            deleted = deletedIds
        )
    }
}