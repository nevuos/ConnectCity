package br.upf.connect_city_api.repository

import br.upf.connect_city_api.model.entity.call.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun existsByName(name: String): Boolean
}
