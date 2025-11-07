@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.lerchenflo.hallenmanager_server.database.model.*
import com.lerchenflo.hallenmanager_server.database.repository.CornerPointRepository
import com.lerchenflo.hallenmanager_server.database.repository.ItemRepository
import com.lerchenflo.hallenmanager_server.util.computeDiffs
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@RestController()
@RequestMapping("/items")
class ItemController(
    private val itemRepository: ItemRepository,
    private val cornerPointRepository: CornerPointRepository,
) {

    data class ItemRequest(
        val itemid: String,
        val areaId: String,
        val title: String,
        val description: String,
        val layers: List<String>,
        val color: Long?,
        val onArea: Boolean,
        val createdAt: String,
        val lastchangedAt: String,
        val lastchangedBy: String,
        val cornerPoints: List<CornerPoint>
    )

    data class ItemResponse(
        val itemid: String,
        val areaId: String,
        val title: String,
        val description: String,
        val color: Long?,
        val layers: List<String>,
        val onArea: Boolean,
        var createdAt: String,
        var lastchangedAt: String,
        var lastchangedBy: String,
        val cornerPoints: List<CornerPointAsSyncObject>
    )


    @PostMapping
    fun upsertItem(
        @RequestParam("username") userName: String,
        @RequestBody body: ItemRequest
    ) : ItemResponse {

        val currentInstant = Clock.System.now().toEpochMilliseconds().toString()
        var item: Item?
        var corners: List<CornerPoint>

        if (body.itemid.isNotEmpty()) {
            //Update existing entry
            val existingEntry = itemRepository.findById(ObjectId(body.itemid)).orElse(null)

            if (existingEntry != null) {
                item = Item(
                    itemid = existingEntry.itemid,
                    title = body.title,
                    description = body.description,
                    createdAt = existingEntry.createdAt,
                    lastchangedAt = currentInstant,
                    lastchangedBy = userName,
                    areaId = body.areaId,
                    color = body.color,
                    layers = body.layers,
                    onArea = body.onArea,
                )

                corners = body.cornerPoints
            }else {
                println("Saved item not found: ${body.itemid}")
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)
            }
        }else {
            item = Item(
                itemid = ObjectId.get(),
                title = body.title,
                description = body.description,
                createdAt = currentInstant,
                lastchangedAt = currentInstant,
                lastchangedBy = userName,
                areaId = body.areaId,
                color = body.color,
                layers = body.layers,
                onArea = body.onArea,
            )

            corners = body.cornerPoints
        }

        val upsertedItem = itemRepository.save(item)


        corners.forEach { cornerPoint ->
            cornerPoint.itemId = upsertedItem.itemid.toHexString()
        }

        val upsertedCorners = cornerPointRepository.saveAll(corners)

        return ItemResponse(
            itemid = upsertedItem.itemid.toHexString(),
            createdAt = upsertedItem.createdAt,
            lastchangedAt = upsertedItem.lastchangedAt,
            lastchangedBy = upsertedItem.lastchangedBy,
            cornerPoints = upsertedCorners.map { cornerPoint ->
                cornerPoint.toCornerPointAsSyncObject()
            },
            areaId = upsertedItem.areaId,
            title = upsertedItem.title,
            description = upsertedItem.description,
            color = upsertedItem.color,
            layers = upsertedItem.layers,
            onArea = upsertedItem.onArea,
        )
    }

    @GetMapping
    fun getItems(): List<Item> {
        return itemRepository.findAll()
    }

    data class ItemSyncResponse(
        val updated: List<ItemResponse>,
        val deleted: List<String>
    )

    @PostMapping("/sync")
    fun itemSync(
        @RequestBody clientList: List<IdTimeStamp>
    ): ItemSyncResponse {
        val localList = itemRepository.findAll()
            .map { item ->
                IdTimeStamp(
                    id = item.itemid,
                    timeStamp = item.lastchangedAt
                )
            }

        val resultTimestamps: List<IdTimeStamp> = computeDiffs(clientList, localList).getAll()
        val changedItemids = resultTimestamps.map { idTimeStamp ->
            idTimeStamp.id
        }

        // Items client has but server doesn't = deleted
        val deletedIds = clientList
            .filter { clientItem ->
                localList.none { it.id == clientItem.id }
            }
            .map { it.id.toString() }

        val cornerpoints = cornerPointRepository.findAllByItemIdIn(changedItemids.map { it.toHexString() })

        val updated = itemRepository.findAllById(changedItemids).map { item ->
            ItemResponse(
                itemid = item.itemid.toHexString(),
                areaId = item.areaId,
                title = item.title,
                description = item.description,
                color = item.color,
                layers = item.layers,
                onArea = item.onArea,
                createdAt = item.createdAt,
                lastchangedAt = item.lastchangedAt,
                lastchangedBy = item.lastchangedBy,
                cornerPoints = cornerpoints.filter { it.itemId == item.itemid.toHexString() }
                    .map { cp -> cp.toCornerPointAsSyncObject() }
            )
        }

        return ItemSyncResponse(
            updated = updated,
            deleted = deletedIds
        )
    }
}