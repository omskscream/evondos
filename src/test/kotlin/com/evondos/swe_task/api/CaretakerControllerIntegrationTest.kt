package com.evondos.swe_task.api

import com.evondos.swe_task.storage.Measurement
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.Instant


@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CaretakerControllerIntegrationTest(
    @param:Autowired val client: WebTestClient,
    @param:Autowired val template: R2dbcEntityTemplate
) {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:18-alpine").apply {
            withDatabaseName("mydatabase")
            withUsername("myuser")
            withPassword("secret")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configure(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgres.host}:${postgres.getMappedPort(5432)}/${postgres.databaseName}"
            }
            registry.add("spring.r2dbc.username", postgres::getUsername)
            registry.add("spring.r2dbc.password", postgres::getPassword)

            // For Flyway/Liquibase (if your project uses it)
            registry.add("spring.flyway.url", postgres::getJdbcUrl)
            registry.add("spring.flyway.user", postgres::getUsername)
            registry.add("spring.flyway.password", postgres::getPassword)
        }
    }

    @BeforeEach
    fun setupData() {
        val m1 = Measurement(
            null,
            Instant.parse("2025-12-07T18:00:00Z"),
            1L,
            130.toShort(), 80.toShort(), 70.toShort()
        )

        val m2 = Measurement(
            null,
            Instant.parse("2025-12-07T18:00:00Z"),
            2L,
            150.toShort(), 95.toShort(), 60.toShort()
        )

        template.delete(Measurement::class.java).all().block()

        template.insert(Measurement::class.java).using(m1).block()
        template.insert(Measurement::class.java).using(m2).block()
    }


    @Test
    fun shouldReturnPatientWithSuspiciousBloodPressure() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/patients/need-attention")
                    .build()
            }
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].patientId").isEqualTo(2L)
            .jsonPath("$[0].sys").isEqualTo(150)
            .jsonPath("$[0].dia").isEqualTo(95)
    }
}