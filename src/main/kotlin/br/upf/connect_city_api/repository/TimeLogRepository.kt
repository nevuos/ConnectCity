package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.TimeLog
import org.springframework.data.jpa.repository.JpaRepository

interface TimeLogRepository : JpaRepository<TimeLog, Long>