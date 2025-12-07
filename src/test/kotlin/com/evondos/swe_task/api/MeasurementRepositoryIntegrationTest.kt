package com.evondos.swe_task.api

import com.evondos.swe_task.storage.Measurement
import com.evondos.swe_task.storage.MeasurementRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import reactor.test.StepVerifier
import java.time.Instant


@Testcontainers(disabledWithoutDocker = true)
@DataR2dbcTest
class MeasurementRepositoryIntegrationTest(
    @param:Autowired val repository: MeasurementRepository,
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
        val patient1BigSys = Measurement(
            null,
            Instant.parse("2025-12-07T18:00:01Z"),
            1L,
            150.toShort(), 80.toShort(), 70.toShort()
        )
        val patient1SmallSys = Measurement(
            null,
            Instant.parse("2025-12-07T18:00:02Z"),
            1L,
            130.toShort(), 90.toShort(), 70.toShort()
        )

        val patient2BigDia = Measurement(
            null,
            Instant.parse("2025-12-07T18:00:01Z"),
            2L,
            130.toShort(), 95.toShort(), 60.toShort()
        )
        val patient2SmallDia = Measurement(
            null,
            Instant.parse("2025-12-07T18:00:02Z"),
            2L,
            130.toShort(), 70.toShort(), 60.toShort()
        )


        template.delete(Measurement::class.java).all().block()

        template.insert(Measurement::class.java).using(patient1BigSys).block()
        template.insert(Measurement::class.java).using(patient1SmallSys).block()
        template.insert(Measurement::class.java).using(patient2BigDia).block()
        template.insert(Measurement::class.java).using(patient2SmallDia).block()
    }


    @Test
    fun `should return biggest systolic measurement for patient1 and biggest diastolic measurement for patient2 `() {
        val start = Instant.parse("2025-12-07T00:00:00Z")
        val end = Instant.parse("2025-12-07T23:59:59Z")

        val patientsFlux = repository.findPatientsNeedingAttentionWithin(start, end)

        StepVerifier.create(patientsFlux)
            .assertNext { result ->
                assert(result.patientId == 1L)
                assert(result.sys == 150.toShort())
                assert(result.dia == 80.toShort())
            }.assertNext { result ->
                assert(result.patientId == 2L)
                assert(result.sys == 130.toShort())
                assert(result.dia == 95.toShort())
            }
            .verifyComplete()
    }
}