package com.evondos.swe_task.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/measurements")
class IngestionController(private val measurementService: MeasurementService) {
    @Operation(
        summary = "Save a measurement",
        description = "Saves a new blood pressure measurement for a patient"
    )
    @ApiResponse(responseCode = "200", description = "Saved measurement")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "500", description = "Maybe trying to save measurement with the same timestamp for a patient twice?")
    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun saveMeasurement(@RequestBody measurement: MeasurementDto): Mono<Void> {
        return measurementService.addMeasurement(measurement)
    }
}