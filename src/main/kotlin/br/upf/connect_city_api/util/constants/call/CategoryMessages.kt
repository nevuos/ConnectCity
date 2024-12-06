package br.upf.connect_city_api.util.constants.call

object CategoryMessages {
    // Mensagens de Categorias
    const val CATEGORY_CREATED_SUCCESS = "Categoria criada com sucesso."
    const val CATEGORY_UPDATED_SUCCESS = "Categoria atualizada com sucesso."
    const val CATEGORY_NOT_FOUND = "Categoria não encontrada com o ID fornecido."
    const val CATEGORY_UPDATED_AND_ACTIVATED_SUCCESS = "Categoria com ID %d atualizada e ativada com sucesso."
    const val CATEGORY_UPDATED_AND_DEACTIVATED_SUCCESS = "Categoria com ID %d atualizada e desativada com sucesso."
    const val CATEGORY_CANNOT_BE_PARENT_OF_ITSELF = "Uma categoria não pode ser pai de si mesma."
    const val CATEGORY_ALREADY_EXISTS = "A categoria já existe."
}