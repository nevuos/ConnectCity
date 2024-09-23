package br.upf.connect_city_api.model.entity.call

import jakarta.persistence.*

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

    @OneToMany(mappedBy = "parentCategory", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var subcategories: MutableList<Category> = mutableListOf()
)