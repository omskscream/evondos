package com.evondos.swe_task.storage

import org.springframework.data.annotation.Id
import java.time.Instant

class Measurement(@Id val id: Long?, val timestamp: Instant, var patientId: Long, val sys: Short = 0, val dia: Short = 0, val pulse: Short = 0)