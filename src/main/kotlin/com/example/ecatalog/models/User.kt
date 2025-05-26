package com.example.ecatalog.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal

@Entity
@Table(name = "catalog_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    var authority: String,
    @field:NotEmpty
    val userName: String,
    @field:NotEmpty
    @JsonIgnore
    val password: String,
    var balance: BigDecimal,
    @OneToMany(cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var itemsBought: MutableList<PurchasedProduct>

) {
    override fun toString(): String {
        return "User(id=$id, userName='$userName', balance=$balance)"
    }
}
