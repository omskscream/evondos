package com.evondos.swe_task.storage

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface MeasurementRepository : ReactiveCrudRepository<Measurement, Long>