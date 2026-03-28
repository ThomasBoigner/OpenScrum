package at.fhtw.openscrum.management.presentation

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

fun createHeadlessChromeDriver(): ChromeDriver {
    val options =
        ChromeOptions().apply {
            addArguments("--headless")
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-gpu")
        }
    return ChromeDriver(options)
}
