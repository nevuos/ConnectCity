package br.upf.connect_city_api.model.entity.address

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Address(
    @Column(nullable = false)
    var street: String,

    @Column(nullable = true)
    var number: String? = null,

    @Column(nullable = true)
    var complement: String? = null,

    @Column(nullable = false)
    var neighborhood: String,

    @Column(nullable = true)
    var city: String? = null,

    @Column(nullable = true)
    var state: String? = null,

    @Column(nullable = true)
    var postalCode: String? = null,

    @Column(nullable = false)
    var country: String = "Brazil"
)