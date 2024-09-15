package br.upf.connect_city_api.model.entity.address

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Address(
    @Column(nullable = false)
    var street: String,

    @Column(nullable = false)
    var number: String,

    @Column(nullable = true)
    var complement: String? = null,

    @Column(nullable = false)
    var neighborhood: String,

    @Column(nullable = false)
    var city: String,

    @Column(nullable = false)
    var state: String,

    @Column(nullable = false)
    var postalCode: String,

    @Column(nullable = false)
    var country: String = "Brazil"
)