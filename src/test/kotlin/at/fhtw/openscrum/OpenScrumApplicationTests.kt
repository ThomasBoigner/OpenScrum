package at.fhtw.openscrum

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.modulith.core.ApplicationModules

@SpringBootTest
class OpenScrumApplicationTests {
    @Test
    fun contextLoads() {
    }

    @Test
    fun verifyModules() {
        ApplicationModules.of(OpenScrumApplication::class.java).verify()
    }
}
