package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CategoryRepository : JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    fun existsByName(name: String): Boolean
}