package com.example.ecatalog

import com.example.ecatalog.models.Product
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EcatalogApplicationTests(
	@Autowired private val testRestTemplate: TestRestTemplate
) {

	@LocalServerPort
	private val port = 8080

	@Test
	fun `Post product with valid id returns valid ResponseEntity`() {
		val url = "http://localhost:$port/products"

		val testProduct = Product(
			name = "product1",
			description = "a new product",
			price = BigDecimal.valueOf(14.99)
		)

		testRestTemplate.postForObject(
			url,
			testProduct,
			String::class.java
		)

	}

	@Test
	fun `GET product with valid id returns valid ResponseEntity`() {
		val url = "http://localhost:$port/products/1"

		val response = testRestTemplate.getForEntity(
			url,
			Product::class.java
		)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(response.body).isNotNull()
		assertThat(response.body?.name).isEqualTo("product1")
	}

}
