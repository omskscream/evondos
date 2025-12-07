package com.evondos.swe_task.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/patients")
class CaretakerController(private val caretakerService: CaretakerService) {
    @Operation(
        summary = "List all patients assigned to the caretaker",
        description = "Returns all patients with their measurements for the currently authenticated caretaker."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of assigned patients returned successfully (might be empty)"
    )
    @GetMapping("/my")
    fun allPatients(): Flux<PatientMeasurementsDto> {
        return caretakerService.findMyPatients()
    }

    @Operation(
        summary = "List patients needing attention",
        description = "Returns patients whose measurements within last 7 days indicate elevated blood pressure."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Patients needing attention returned successfully"
    )
    @GetMapping("/need-attention")
    fun needAttentionPatients(): Flux<MeasurementDto> {
        return caretakerService.findPatientsNeedingAttention()
    }

}