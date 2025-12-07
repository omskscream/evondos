package com.evondos.swe_task.api

import java.time.Instant

data class MeasurementDto(val patientId: Long, val timestamp: Instant, val sys: Short, val dia: Short, val pulse: Short)

data class PatientMeasurementsDto(val id: Long, val measurements: List<MeasurementDto>)

data class PatientStatusDto(val id: Long, val pressureStatus: BloodPressureCategory, val pulseStatus: PulseCategory)

enum class BloodPressureCategory {
    LOW,        // s<90 || d<60
    NORMAL,     // 90<s<120 || 60<d<80
    ELEVATED,   // 120<s<140 || 80<d<90
    HIGH,       // 140<s || 90<d
}

enum class PulseCategory {
    HIGH, //>100
    LOW,  //<60
}