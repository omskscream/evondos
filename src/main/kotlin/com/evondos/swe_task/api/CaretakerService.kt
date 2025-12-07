package com.evondos.swe_task.api

import com.evondos.swe_task.storage.Measurement
import com.evondos.swe_task.storage.MeasurementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import java.time.Instant
import java.time.temporal.ChronoUnit

@Transactional
@Service
class CaretakerService(private val measurementRepository: MeasurementRepository) {

    fun findMyPatients(): Flux<PatientMeasurementsDto> {
        return measurementRepository.findAll()
            .groupBy(Measurement::patientId)
            .flatMap { patientToDataFlux -> patientToDataFlux.collectList()
                .map { measurements -> measurements.map { m -> convertMeasurementFromDb(m) } }
                .map { measurementDtoList -> PatientMeasurementsDto(patientToDataFlux.key(), measurementDtoList) }
            }
    }

    fun findPatientsNeedingAttention(): Flux<MeasurementDto> {
        val timeRangeEnd = Instant.now();
        val timeRangeStart = timeRangeEnd.minus(7, ChronoUnit.DAYS)
        return measurementRepository.findPatientsNeedingAttentionWithin(timeRangeStart, timeRangeEnd)
            .map { convertMeasurementFromDb(it) }
    }
}

private fun convertMeasurementFromDb(m: Measurement): MeasurementDto {
    return MeasurementDto(m.patientId, m.timestamp, m.sys, m.dia, m.pulse)
}