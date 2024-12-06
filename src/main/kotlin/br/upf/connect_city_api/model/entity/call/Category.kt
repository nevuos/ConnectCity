package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", nullable = true)
    var parentCategory: Category? = null,

    @OneToMany(mappedBy = "parentCategory", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var subcategories: MutableList<Category> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, updatable = false)
    var createdBy: String,

    @Column(nullable = false)
    var isActive: Boolean = true
)