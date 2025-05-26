package com.example.ecatalog.services

import com.example.ecatalog.models.Product
import com.example.ecatalog.models.PurchasedProduct
import com.example.ecatalog.models.UserDetailsAdapter
import com.example.ecatalog.repositories.PurchasedProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class PurchasedProductService {
    @Autowired
    lateinit var purchasedProductRepository: PurchasedProductRepository

    @Autowired
    @Lazy
    lateinit var productService: ProductService

    @Autowired
    lateinit var userService: UserService

    fun save(purchasedProduct: PurchasedProduct): PurchasedProduct {
        return purchasedProductRepository.save(purchasedProduct)
    }

    fun delete(purchasedProduct: PurchasedProduct) {
        purchasedProductRepository.delete(purchasedProduct)
    }

    fun deleteById(id: Long) {
        purchasedProductRepository.deleteById(id)
    }

    fun getAllPurchasedProducts(): List<PurchasedProduct> {
        return purchasedProductRepository.findAll()
    }

    fun getById(id: Long): Optional<PurchasedProduct> {
        return purchasedProductRepository.findById(id)
    }

    fun returnItem(
        userDetails: UserDetailsAdapter,
        id: Long
    ): ResponseEntity<*> {
        val optionalProduct = getById(id)
        val product = optionalProduct.getOrNull()
        var error = "Product with id $id not found"
        if (product == null) {
            return ResponseEntity(error, HttpStatus.NOT_FOUND)
        }

        error = "Product with id $id does not belong to ${userDetails.username}"
        if (product.ownerName != userDetails.username) {
            return ResponseEntity(error, HttpStatus.BAD_REQUEST)
        }

        val user = userService.findByUsername(userDetails.username).getOrNull()
            ?: return ResponseEntity(
                "User ${userDetails.username} not found",
                HttpStatus.NOT_FOUND
            )

        user.balance += product.price
        user.itemsBought.remove(product)

        val soldProduct = Product(
            name = product.name,
            description = product.description,
            price = product.price
        )

        productService.save(soldProduct)

        delete(product)

        return ResponseEntity("$soldProduct\nsold", HttpStatus.OK)
    }

}
