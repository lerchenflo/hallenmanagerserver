@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.lerchenflo.hallenmanager_server.database.model.Area
import com.lerchenflo.hallenmanager_server.database.model.AreaAsSyncObject
import com.lerchenflo.hallenmanager_server.database.model.IdTimeStamp
import com.lerchenflo.hallenmanager_server.database.model.Layer
import com.lerchenflo.hallenmanager_server.database.model.asSyncObject
import com.lerchenflo.hallenmanager_server.database.repository.AreaRepository
import com.lerchenflo.hallenmanager_server.database.repository.LayerRepository
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
@RequestMapping("/layers")
class LayerController(
    private val layerRepository: LayerRepository
) {

    data class LayerRequest(
        val layerid: String,
        val name: String,
        val sortId: Int,
        val shown: Boolean,
        val color: Long,
    )

    data class LayerResponse(
        val layerid: String,
        val name: String,
        val sortId: Int,
        val shown: Boolean,
        val color: Long,
        var createdAt: String,
        var lastchangedAt: String,
        var lastchangedBy: String,
    )

    @PostMapping
    fun upsertLayer(
        @RequestParam("username") userName: String,
        @RequestBody body: LayerRequest
    ) : LayerResponse {

        val currentInstant = Clock.System.now().toEpochMilliseconds().toString()
        var layer: Layer?

        println("Requestbody: $body")

        if (body.layerid.isNotEmpty()) {
            //Update existing entry
            val existingEntry = layerRepository.findById(ObjectId(body.layerid)).orElse(null)

            println("ExistingLayer: $existingEntry")

            if (existingEntry != null) {
                layer = Layer(
                    layerid = existingEntry.layerid,
                    name = body.name,
                    sortId = body.sortId,
                    shown = body.shown,
                    color = body.color,
                    createdAt = existingEntry.createdAt,
                    lastchangedAt = currentInstant,
                    lastchangedBy = userName,
                )
            } else {
                println("An bad request")
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            }
        }else {
            layer = Layer(
                layerid = ObjectId.get(),
                name = body.name,
                sortId = body.sortId,
                shown = body.shown,
                color = body.color,
                createdAt = currentInstant,
                lastchangedAt = currentInstant,
                lastchangedBy = userName,
            )
        }

        val upsertedLayer = layerRepository.save(layer)

        return LayerResponse(
            layerid = upsertedLayer.layerid.toString(),
            name = upsertedLayer.name,
            sortId = upsertedLayer.sortId,
            shown = upsertedLayer.shown,
            color = upsertedLayer.color,
            createdAt = upsertedLayer.createdAt,
            lastchangedAt = upsertedLayer.lastchangedAt,
            lastchangedBy = upsertedLayer.lastchangedBy,
        )
    }

    @GetMapping
    fun getLayers(): List<Layer> {
        return layerRepository.findAll()
    }





    @PostMapping("/sync")
    fun layerSync(
        @RequestBody clientList: List<IdTimeStamp>
    ) : List<LayerResponse> {
        val localList = layerRepository.findAll()
            .map { layer ->
                IdTimeStamp(
                    id = layer.layerid,
                    timeStamp = layer.lastchangedAt
                )
        }

        val resultTimestamps: List<IdTimeStamp> = computeDiffs(clientList, localList).getAll()
        val changedLayerids = resultTimestamps.map {
            it.id
        }

        println("Changed Layer ids: $changedLayerids")

        return layerRepository.findAllById(changedLayerids).map { layer ->
            LayerResponse(
                layerid = layer.layerid.toString(),
                name = layer.name,
                sortId = layer.sortId,
                shown = layer.shown,
                color = layer.color,
                createdAt = layer.createdAt,
                lastchangedAt = layer.lastchangedAt,
                lastchangedBy = layer.lastchangedBy,
            )
        }
    }
}