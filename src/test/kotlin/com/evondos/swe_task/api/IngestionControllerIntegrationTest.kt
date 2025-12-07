package com.evondos.swe_task.api

import com.evondos.swe_task.storage.Measurement
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IngestionControllerIntegrationTest(
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
    fun cleanDb() {
        template.delete(Measurement::class.java).all().block()
    }

    @Test
    fun `should save measurement and return saved entity`() {
        val payload = mapOf(
            "patientId" to 123,
            "timestamp" to "2025-12-07T18:00:00Z",
            "sys" to 150,
            "dia" to 95,
            "pulse" to 72
        )

        val responseMeasurement = client.post()
            .uri("/measurements")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk


        val savedMeasurement = template.select(Measurement::class.java)
            .matching(
                Query.query(
                    Criteria.where("patient_id").`is`(123L)
                )
            )
            .one()
            .block()

        assert(savedMeasurement != null)
        assert(savedMeasurement?.sys == 150.toShort())
        assert(savedMeasurement?.dia == 95.toShort())
    }
}