package br.upf.connect_city_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class SystemConnectCityApi

fun main(args: Array<String>) {
    runApplication<SystemConnectCityApi>(*args)
}
