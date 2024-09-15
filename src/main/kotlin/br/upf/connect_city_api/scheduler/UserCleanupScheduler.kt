package br.upf.connect_city_api.scheduler


import br.upf.connect_city_api.service.infrastructure.UserCleanupService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UserCleanupScheduler(private val userCleanupService: UserCleanupService) {

    @Scheduled(cron = "0 0 0 * * ?")
    fun runCleanup() {
        userCleanupService.cleanupExpiredTemporaryUsers()
    }
}
