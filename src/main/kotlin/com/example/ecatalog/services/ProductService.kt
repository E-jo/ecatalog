package com.example.ecatalog.services

import com.example.ecatalog.models.ChangeRequest
import com.example.ecatalog.models.Product
import com.example.ecatalog.models.PurchasedProduct
import com.example.ecatalog.models.UserDetailsAdapter
import com.example.ecatalog.repositories.ProductRepository
import com.example.ecatalog.repositories.PurchasedProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val purchasedProductService: PurchasedProductService,
    private val userService: UserService
) {

    val productsApiUrl = "http://localhost:8080/products/"
    val log: Logger = Logger.getAnonymousLogger()


    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    fun getById(id: Long): Optional<Product> {
        return productRepository.findById(id)
    }

    fun save(product: Product): Product {
        return productRepository.save(product)
    }

    fun delete(id: Long) {
        productRepository.deleteById(id)
    }

    fun buyItem(
        userDetails: UserDetailsAdapter,
        id: Long
    ): ResponseEntity<*> {
        val optionalProduct = getById(id)
        val product = optionalProduct.getOrNull()
        val error = "Product with id $id not found"
        if (product == null) {
            return ResponseEntity(error, HttpStatus.NOT_FOUND)
        }

        log.info("Product $id found")

        val userOptional = userService.findByUsername(userDetails.username)
        if (userOptional.isEmpty) {
            return ResponseEntity(
                "User ${userDetails.username} not found",
                HttpStatus.NOT_FOUND
            )
        }
        val user = userOptional.get()

        log.info("user ${user.userName} found")

        if (user.balance < product.price) {
            return ResponseEntity(
                "Insufficient balance",
                HttpStatus.BAD_REQUEST
            )
        }

        log.info("sufficient balance for purchase")

        val purchasedProduct = PurchasedProduct(
            id = null,
            name = product.name,
            description = product.description,
            price = product.price,
            owner = user,
            ownerName = user.userName
        )
        log.info("PurchasedProduct object created")

        purchasedProductService.save(purchasedProduct)

        user.itemsBought.add(purchasedProduct)
        log.info("PurchasedProduct added to ${user.userName}'s itemsBought")

        user.balance -= product.price
        log.info("balance adjusted to ${user.balance}")

        log.info("$purchasedProduct")
        delete(product.id!!)
        log.info("product deleted from Product db")

        userService.save(user)
        log.info("changes to user ${user.userName} saved")

        return ResponseEntity("$product\npurchased", HttpStatus.OK)
    }

    fun updateProduct(
        id: Long,
        changeRequest: ChangeRequest
    ): ResponseEntity<*> {

        val optionalProduct = getById(id)
        val product = optionalProduct.getOrNull()
        val error = "Product with id $id not found"
        if (product == null) {
            return ResponseEntity(error, HttpStatus.NOT_FOUND)
        }
        if (changeRequest.name != null) {
            product.name = changeRequest.name
        }
        if (changeRequest.description != null) {
            product.description = changeRequest.description
        }
        if (changeRequest.price != null) {
            product.price = changeRequest.price
        }

        save(product)
        return ResponseEntity(product, HttpStatus.OK)
    }

    fun createProduct(newProduct: Product): ResponseEntity<*> {
        val product = Product(
            name = newProduct.name,
            description = newProduct.description,
            price = newProduct.price
        )
        save(product)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(
                "Location",
                "$productsApiUrl${product.id}"
            )
            .body("$product \nsaved")
    }

    fun getProductById(id: Long): ResponseEntity<*> {
        val optionalProduct = getById(id)
        val product = optionalProduct.getOrNull()
        val error = "Product with id $id not found"
        return if (product == null) {
            ResponseEntity(error, HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity.ok(product)
        }
    }
}