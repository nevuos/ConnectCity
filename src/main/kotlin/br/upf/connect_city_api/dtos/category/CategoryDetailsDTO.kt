package br.upf.connect_city_api.dtos.category

import java.time.LocalDateTime

data class CategoryDetailsDTO(
    val id: Long? = null,
    val name: String? = null,
    val parentCategory: CategorySummaryDTO? = null,
    val subcategories: List<CategorySummaryDTO>? = null,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val isActive: Boolean? = null
)

data class CategorySummaryDTO(
    val id: Long? = null,
    val name: String? = null,
    val isActive: Boolean? = null,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null
)