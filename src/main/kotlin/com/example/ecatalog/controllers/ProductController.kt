package com.example.ecatalog.controllers

import com.example.ecatalog.models.ChangeRequest
import com.example.ecatalog.models.Product
import com.example.ecatalog.models.UserDetailsAdapter
import com.example.ecatalog.services.ProductService
import com.example.ecatalog.services.PurchasedProductService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class ProductController(
    private val productService: ProductService,
    private val purchasedProductService: PurchasedProductService
) {

    @GetMapping(path = ["/products"])
    fun getAllProducts(): ResponseEntity<*> {
        return ResponseEntity.ok(
            productService.getAllProducts()
        )
    }

    @GetMapping(path = ["/products/purchased"])
    fun getAllPurchasedProducts(): ResponseEntity<*> {
        return ResponseEntity.ok(
            purchasedProductService.getAllPurchasedProducts()
        )
    }

    @GetMapping(path = ["/products/{id}"])
    fun getProductById(@PathVariable id: Long): ResponseEntity<*> {
        return productService.getProductById(id)
    }

    @PostMapping(path = ["/products"])
    fun createProduct(@Valid @RequestBody newProduct: Product): ResponseEntity<*> {
        return productService.createProduct(newProduct)
    }

    @PostMapping(path = ["/products/{id}"])
    fun buyItem(
        @AuthenticationPrincipal userDetails: UserDetailsAdapter,
        @PathVariable id: Long
    ): ResponseEntity<*> {
        return productService.buyItem(userDetails, id)
    }

    @PostMapping(path = ["/products/sell/{id}"])
    fun returnItem(
        @AuthenticationPrincipal userDetails: UserDetailsAdapter,
        @PathVariable id: Long
    ): ResponseEntity<*> {
        return purchasedProductService.returnItem(userDetails, id)
    }

    @DeleteMapping(path = ["/products/{id}"])
    fun deleteProduct(@PathVariable id: Long) {
        productService.delete(id)
    }

    @PutMapping(path = ["/products/{id}"])
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody changeRequest: ChangeRequest
    ): ResponseEntity<*> {
        return productService.updateProduct(id, changeRequest)
    }

}

