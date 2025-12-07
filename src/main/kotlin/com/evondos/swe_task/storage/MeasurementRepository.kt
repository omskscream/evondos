package com.evondos.swe_task.storage

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.time.Instant

interface MeasurementRepository : ReactiveCrudRepository<Measurement, Long> {
    @Query(
        """
        SELECT mr.timestamp, mr.patient_id, mr.sys, mr.dia, mr.pulse
        FROM (
            SELECT m.timestamp, m.patient_id, m.sys, m.dia, m.pulse,
                   ROW_NUMBER() OVER (
                        PARTITION BY m.patient_id
                        ORDER BY m.sys DESC, m.dia DESC, m.timestamp DESC
                   ) AS row
            FROM measurement m
            WHERE m.timestamp BETWEEN :timeRangeStart AND :timeRangeEnd
            AND (m.sys > 140 OR m.dia > 90)
        ) mr
        WHERE row = 1
        ORDER BY mr.patient_id;

    """
    )
    fun findPatientsNeedingAttentionWithin(timeRangeStart: Instant, timeRangeEnd: Instant): Flux<Measurement>
}