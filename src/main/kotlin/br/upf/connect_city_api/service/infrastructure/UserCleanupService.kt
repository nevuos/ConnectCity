package br.upf.connect_city_api.service.infrastructure

import br.upf.connect_city_api.repository.UserRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class UserCleanupService(private val temporaryUserRepository: UserRepository) {

    @Async
    fun cleanupExpiredTemporaryUsers(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val thresholdDate = LocalDateTime.now().minusDays(7)
            temporaryUserRepository.findExpiredUsers(thresholdDate).forEach {
                temporaryUserRepository.delete(it)
            }
        }
    }
}