package br.upf.connect_city_api.service.infrastructure

import br.upf.connect_city_api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class UserCleanupService(private val temporaryUserRepository: UserRepository) {

    private val logger = LoggerFactory.getLogger(UserCleanupService::class.java)

    @Async
    @Transactional
    fun cleanupExpiredTemporaryUsers(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            try {
                val thresholdDate = LocalDateTime.now().minusDays(7)
                val expiredUsers = temporaryUserRepository.findExpiredUsers(thresholdDate)
                expiredUsers.forEach { user ->
                    temporaryUserRepository.delete(user)
                    logger.info("Usuário temporário expirado excluído: ${user.id}")
                }
            } catch (e: Exception) {
                logger.error("Erro ao limpar usuários temporários expirados", e)
            }
        }
    }
}