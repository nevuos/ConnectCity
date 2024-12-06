package br.upf.connect_city_api.service.call.category

import br.upf.connect_city_api.dtos.category.CategoryDetailsDTO
import br.upf.connect_city_api.dtos.category.CreateCategoryRequestDTO
import br.upf.connect_city_api.dtos.category.UpdateCategoryRequestDTO
import br.upf.connect_city_api.model.entity.call.Category
import br.upf.connect_city_api.repository.CategoryRepository
import br.upf.connect_city_api.repository.specifications.CategorySpecifications
import br.upf.connect_city_api.service.auth.TokenService
import br.upf.connect_city_api.util.constants.call.CategoryMessages
import br.upf.connect_city_api.util.exception.InvalidRequestError
import br.upf.connect_city_api.util.exception.ResourceNotFoundError
import jakarta.servlet.http.HttpServletRequest
import org.hibernate.Hibernate
import org.modelmapper.ModelMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val tokenService: TokenService,
    private val modelMapper: ModelMapper
) {

    @Transactional
    @CacheEvict(value = ["searchCategories", "categoryById"], allEntries = true, cacheManager = "searchCacheManager")
    fun createCategory(request: HttpServletRequest, createRequest: CreateCategoryRequestDTO): String {
        if (categoryRepository.existsByName(createRequest.name)) {
            throw InvalidRequestError(CategoryMessages.CATEGORY_ALREADY_EXISTS)
        }

        val user = tokenService.getUserFromRequest(request)

        val category = Category(
            name = createRequest.name,
            createdBy = user.username,
        )

        createRequest.parentCategoryId?.let { parentId ->
            if (parentId == 0L) {
                // Remove a categoria pai, definindo como null
                category.parentCategory = null
            } else {
                val parentCategory = getCategoryById(parentId)
                if (parentCategory.id == category.id) {
                    throw InvalidRequestError(CategoryMessages.CATEGORY_CANNOT_BE_PARENT_OF_ITSELF)
                }
                category.parentCategory = parentCategory
            }
        }

        categoryRepository.save(category)
        return CategoryMessages.CATEGORY_CREATED_SUCCESS.format(user.username)
    }

    @Transactional
    @CacheEvict(value = ["searchCategories", "categoryById"], allEntries = true, cacheManager = "searchCacheManager")
    fun updateCategory(categoryId: Long, updateRequest: UpdateCategoryRequestDTO?, isActive: Boolean?): String {
        val category = getCategoryById(categoryId)

        updateRequest?.name?.let {
            category.name = it
        }

        updateRequest?.parentCategoryId?.let { parentCategoryId ->
            if (parentCategoryId == 0L) {
                category.parentCategory = null
            } else {
                val parentCategory = getCategoryById(parentCategoryId)
                if (parentCategory.id == category.id) {
                    throw InvalidRequestError(CategoryMessages.CATEGORY_CANNOT_BE_PARENT_OF_ITSELF)
                }
                category.parentCategory = parentCategory
            }
        }

        isActive?.let {
            category.isActive = it
        }

        categoryRepository.save(category)

        return when (isActive) {
            true -> CategoryMessages.CATEGORY_UPDATED_AND_ACTIVATED_SUCCESS.format(categoryId)
            false -> CategoryMessages.CATEGORY_UPDATED_AND_DEACTIVATED_SUCCESS.format(categoryId)
            else -> CategoryMessages.CATEGORY_UPDATED_SUCCESS
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(
        value = ["searchCategories"],
        key = "{#name, #parentCategoryId, #createdAfter, #createdBefore, #createdOn, #isActive, #createdBy, #pageable}",
        cacheManager = "searchCacheManager"
    )
    fun searchCategories(
        name: String?,
        parentCategoryId: Long?,
        createdAfter: LocalDateTime?,
        createdBefore: LocalDateTime?,
        createdOn: LocalDate?,
        isActive: Boolean?,
        createdBy: String?,
        pageable: Pageable
    ): Page<CategoryDetailsDTO> {
        val spec = createCategorySpecification(
            name,
            parentCategoryId,
            createdAfter,
            createdBefore,
            createdOn,
            isActive,
            createdBy
        )

        return categoryRepository.findAll(spec, pageable).map { category ->
            Hibernate.initialize(category.subcategories)
            Hibernate.initialize(category.parentCategory)
            modelMapper.map(category, CategoryDetailsDTO::class.java)
        }
    }

    @Transactional(readOnly = true)
    private fun createCategorySpecification(
        name: String?,
        parentCategoryId: Long?,
        createdAfter: LocalDateTime?,
        createdBefore: LocalDateTime?,
        createdOn: LocalDate?,
        isActive: Boolean?,
        createdBy: String?
    ): Specification<Category> {
        return Specification.where(CategorySpecifications.nameContains(name))
            .and(CategorySpecifications.parentCategoryEquals(parentCategoryId))
            .and(CategorySpecifications.createdAfter(createdAfter))
            .and(CategorySpecifications.createdBefore(createdBefore))
            .and(CategorySpecifications.createdOn(createdOn))
            .and(CategorySpecifications.isActive(isActive))
            .and(CategorySpecifications.createdByEquals(createdBy))
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["categoryById"], key = "#categoryId", cacheManager = "searchCacheManager")
    private fun getCategoryById(categoryId: Long): Category {
        return categoryRepository.findById(categoryId).orElseThrow {
            throw ResourceNotFoundError(CategoryMessages.CATEGORY_NOT_FOUND.format(categoryId))
        }
    }
}