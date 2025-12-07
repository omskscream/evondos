package com.evondos.swe_task.api

import com.evondos.swe_task.storage.Measurement
import com.evondos.swe_task.storage.MeasurementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Transactional
@Service
class MeasurementService(private val measurementRepository: MeasurementRepository) {

    fun addMeasurement(measurement: MeasurementDto): Mono<Void> {
        return measurementRepository.save(convertMeasurementToDb(measurement)).then()
    }
}

private fun convertMeasurementToDb(m: MeasurementDto): Measurement {
    return Measurement(null,m.timestamp, m.patientId, m.sys, m.dia, m.pulse)
}