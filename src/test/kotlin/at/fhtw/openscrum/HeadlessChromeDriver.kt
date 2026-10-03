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
            setExperimentalOption(
                "prefs",
                mapOf(
                    "credentials_enable_service" to false,
                    "profile.password_manager_enabled" to false,
                    "profile.password_manager_leak_detection" to false,
                ),
            )
        }
    return ChromeDriver(options)
}
