@file:OptIn(ExperimentalTime::class)

package com.lerchenflo.hallenmanager_server.controllers

import com.lerchenflo.hallenmanager_server.database.model.Area
import com.lerchenflo.hallenmanager_server.database.repository.AreaRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@RestController()
@RequestMapping()
class MainController(
    private val areaRepository: AreaRepository
) {

    @GetMapping
    @RequestMapping("/test")
    fun test(): String {
        return "server found"
    }


    @EventListener(ApplicationReadyEvent::class)
    fun createDefaultArea() {
        if (areaRepository.count() == 0L) {

            //First start with no areas, create the first one
            areaRepository.insert(Area(
                name = "remoteArea1",
                description = "Default area",
                createdAt = Clock.System.now(),
                lastchangedAt = Clock.System.now(),
                lastchangedBy = "server"
            ))

            println("first start area created")
        }
    }
}