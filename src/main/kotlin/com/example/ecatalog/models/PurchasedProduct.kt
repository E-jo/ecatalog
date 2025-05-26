package com.example.ecatalog.models

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity
data class PurchasedProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @field:NotBlank(message = "Product must have a name")
    @field:NotEmpty(message = "Product must have a name")
    var name: String,
    var description: String?,
    @field:PositiveOrZero(message = "Price must be non-negative")
    var price: BigDecimal,
    @ManyToOne(cascade = [CascadeType.DETACH])
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var owner: User,
    @JsonAlias("owner")
    var ownerName: String
) {
    override fun toString(): String {
        return "PurchasedProduct(id=$id, name='$name', description='$description', price=$price, owner=${owner.userName})"
    }
}

