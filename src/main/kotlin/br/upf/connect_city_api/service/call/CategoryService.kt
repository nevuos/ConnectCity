package br.upf.connect_city_api.service.call

import br.upf.connect_city_api.dtos.call.CreateCategoryRequestDTO
import br.upf.connect_city_api.model.entity.call.Category
import br.upf.connect_city_api.repository.CategoryRepository
import br.upf.connect_city_api.util.constants.call.CallMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val modelMapper: ModelMapper
) {

    @Transactional
    fun createCategory(createRequest: CreateCategoryRequestDTO): String {
        if (categoryRepository.existsByName(createRequest.name)) {
            throw InvalidRequestError(CallMessages.CATEGORY_ALREADY_EXISTS)
        }

        val category = modelMapper.map(createRequest, Category::class.java)
        categoryRepository.save(category)

        return CallMessages.CATEGORY_CREATED_SUCCESS
    }

    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    fun getCategoryById(categoryId: Long): Category {
        return categoryRepository.findById(categoryId).orElseThrow {
            throw ResourceNotFoundError(CallMessages.CATEGORY_NOT_FOUND)
        }
    }

    @Transactional
    fun updateCategory(categoryId: Long, createRequest: CreateCategoryRequestDTO): String {
        val category = getCategoryById(categoryId)
        category.name = createRequest.name
        categoryRepository.save(category)
        return CallMessages.CATEGORY_UPDATED_SUCCESS
    }

    @Transactional
    fun deleteCategory(categoryId: Long): String {
        val category = getCategoryById(categoryId)
        categoryRepository.delete(category)
        return CallMessages.CATEGORY_DELETED_SUCCESS
    }
}