package com.example.ecatalog.repositories

import com.example.ecatalog.models.PurchasedProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchasedProductRepository : JpaRepository<PurchasedProduct, Long> {
}