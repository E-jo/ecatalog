package com.example.ecatalog.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @field:NotBlank(message = "Product must have a name")
    @field:NotEmpty(message = "Product must have a name")
    var name: String,
    var description: String?,
    @field:PositiveOrZero(message = "Price must be non-negative")
    var price: BigDecimal
)