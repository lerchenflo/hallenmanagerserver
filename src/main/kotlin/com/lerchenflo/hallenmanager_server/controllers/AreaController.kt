@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.lerchenflo.hallenmanager_server.database.model.Area
import com.lerchenflo.hallenmanager_server.database.repository.AreaRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@RestController
@RequestMapping("/area")
class AreaController(
    private val areaRepository: AreaRepository
) {

    data class AreaRequest(
        val id: String?,
        val name: String,
        val description: String,
    )


    @PostMapping
    fun postmapping(
        @RequestParam("username") userName: String,
        @RequestBody body: AreaRequest
    ) : Area {

        var area: Area?

        if (body.id != null) {
            //Update existing entry
            val existingEntry = areaRepository.findById(ObjectId(body.id)).orElse(null)

            if (existingEntry != null) {
                area = Area(
                    id = existingEntry.id,
                    name = body.name,
                    description = body.description,
                    createdAt = existingEntry.createdAt,
                    lastchangedAt = Clock.System.now().toEpochMilliseconds(),
                    lastchangedBy = userName
                )
            }else {
                throw IllegalStateException("There is no area with id ${body.id}")
            }
        }else {
            area = Area(
                id = ObjectId.get(),
                name = body.name,
                description = body.description,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                lastchangedAt = Clock.System.now().toEpochMilliseconds(),
                lastchangedBy = userName,
            )
        }

        return areaRepository.save(
            area
        )
    }

}