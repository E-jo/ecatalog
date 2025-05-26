package com.example.ecatalog

import com.example.ecatalog.models.Product
import jakarta.validation.Validation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.event.EventListener
import java.math.BigDecimal

@SpringBootApplication
@ComponentScan(basePackages = ["com.example.ecatalog"])
class EcatalogApplication

@EventListener(ApplicationReadyEvent::class)
fun validate() {
	val validator = Validation.buildDefaultValidatorFactory().validator
	val violations = validator.validate(Product(name = "", description = null, price = BigDecimal(-1)))
	println("Violations: ${violations.map { it.message }}")
}
fun main(args: Array<String>) {
	runApplication<EcatalogApplication>(*args)
}


