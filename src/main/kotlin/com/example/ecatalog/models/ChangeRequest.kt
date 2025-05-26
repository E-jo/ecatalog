package com.example.ecatalog.models

import java.math.BigDecimal

data class ChangeRequest(
    val name: String?,
    val description: String?,
    val price: BigDecimal?
) {
}