package at.fhtw.openscrum

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpenScrumApplication

fun main(args: Array<String>) {
    runApplication<OpenScrumApplication>(*args)
}
