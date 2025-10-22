package com.lerchenflo.hallenmanager_server.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@SpringBootApplication
@ComponentScan(basePackages = ["com.lerchenflo.hallenmanager_server"])
@EnableMongoRepositories(basePackages = ["com.lerchenflo.hallenmanager_server.database.repository"])
class HallenmanagerServerApplication

fun main(args: Array<String>) {
    runApplication<HallenmanagerServerApplication>(*args)
}