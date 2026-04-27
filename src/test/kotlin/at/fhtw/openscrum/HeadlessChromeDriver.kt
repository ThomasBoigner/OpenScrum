package at.fhtw.openscrum

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

fun createHeadlessChromeDriver(): ChromeDriver {
    val options =
        ChromeOptions().apply {
            addArguments("--headless")
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-gpu")
            addArguments("--window-size=1920,1080")
        }
    return ChromeDriver(options)
}
