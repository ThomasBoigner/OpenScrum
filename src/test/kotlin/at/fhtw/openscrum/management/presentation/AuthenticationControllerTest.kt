package at.fhtw.openscrum.management.presentation

import at.fhtw.openscrum.E2ETest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

class AuthenticationControllerTest : E2ETest() {
    /*
    Given an email and a password
    When I enter the email and password
    Then I want to be logged in
     */
    @Test
    fun ensureLoginWorksProperly() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        userService.registerUser(
            authenticatedUser = admin,
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
        )

        // When
        login(username, password)

        // Then
        assertThat(webDriver.currentUrl).isEqualTo("$baseUrl/projects")
    }

    /*
    Given an email and a password
    When I enter the wrong email
    Then I receive an error
     */
    @Test
    fun ensureLoginFailsWithWrongEmail() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        userService.registerUser(
            authenticatedUser = admin,
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
        )

        // When
        webDriver.get(baseUrl)
        webDriver.findElement(By.cssSelector("input#username")).sendKeys("Wrong Username")
        webDriver.findElement(By.cssSelector("input#password")).sendKeys(password)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()

        // Then
        assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))).isNotNull
    }

    /*
    Given an email and a password
    When I enter the wrong password
    Then I receive an error
     */
    @Test
    fun ensureLoginFailsWithWrongPassword() {
        // Given
        val username = "john.doe"
        val password = "abc123"

        userService.registerUser(
            authenticatedUser = admin,
            username = username,
            firstName = "John",
            lastName = "Doe",
            password = password,
            email = "john.doe@gmail.com",
        )

        // When
        webDriver.get(baseUrl)
        webDriver.findElement(By.cssSelector("input#username")).sendKeys(username)
        webDriver.findElement(By.cssSelector("input#password")).sendKeys("Wrong Password")
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("section#login-form button"))).click()

        // Then
        assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.error-message")))).isNotNull
    }
}
