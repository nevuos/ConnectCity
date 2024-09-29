package br.upf.connect_city_api.service.call

import br.upf.connect_city_api.model.entity.call.Interaction
import br.upf.connect_city_api.repository.CallRepository
import br.upf.connect_city_api.repository.InteractionRepository
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InteractionService(
    private val interactionRepository: InteractionRepository,
    private val callRepository: CallRepository
) {
    private val logger = LoggerFactory.getLogger(InteractionService::class.java)

    @Transactional
    fun createInteraction(callId: Long, updatedBy: String, updateDetails: String): String {
        val call = callRepository.findById(callId)
            .orElseThrow { throw ResourceNotFoundError(CallMessages.CALL_NOT_FOUND) }

        val interaction = Interaction(
            call = call,
            date = LocalDateTime.now(),
            updatedBy = updatedBy,
            updateDetails = updateDetails
        )
        interactionRepository.save(interaction)
        logger.info("Interaction created for callId: ${call.id} by user: $updatedBy")
        return CallMessages.INTERACTION_ADDED_SUCCESSFULLY
    }

    fun getInteractionById(interactionId: Long): Interaction {
        return interactionRepository.findById(interactionId).orElseThrow {
            logger.error(CallMessages.INTERACTION_NOT_FOUND)
            throw ResourceNotFoundError(CallMessages.INTERACTION_NOT_FOUND)
        }
    }

    fun getAllInteractionsByCall(callId: Long): List<Interaction> {
        return interactionRepository.findByCallId(callId).ifEmpty {
            throw ResourceNotFoundError(CallMessages.INTERACTION_NOT_FOUND)
        }
    }

    @Transactional
    fun updateInteraction(interactionId: Long, updatedBy: String, updateDetails: String): Interaction {
        val interaction = getInteractionById(interactionId)
        interaction.updatedBy = updatedBy
        interaction.updateDetails = updateDetails
        interaction.date = LocalDateTime.now()

        interactionRepository.save(interaction)
        logger.info("Interaction updated for interactionId: $interactionId by user: $updatedBy")
        return interaction
    }

    @Transactional
    fun deleteInteraction(callId: Long, interactionId: Long): String {
        val interaction = interactionRepository.findById(interactionId)
            .orElseThrow { throw ResourceNotFoundError(CallMessages.INTERACTION_NOT_FOUND) }

        if (interaction.call.id != callId) {
            throw InvalidRequestError(CallMessages.INTERACTION_NOT_BELONG_TO_CALL)
        }

        interactionRepository.delete(interaction)
        logger.info("Interaction deleted for interactionId: $interactionId")
        return CallMessages.INTERACTION_REMOVED_SUCCESSFULLY
    }

}