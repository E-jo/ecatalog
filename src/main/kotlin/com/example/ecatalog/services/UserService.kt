package com.example.ecatalog.services

import com.example.ecatalog.models.User
import com.example.ecatalog.models.UserDetailsAdapter
import com.example.ecatalog.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

@Service
class UserService : UserDetailsService {

    val log: Logger = Logger.getAnonymousLogger()

    @Autowired
    lateinit var userRepository: UserRepository

    fun findAll(): List<User> {
        return userRepository.findAll()
    }

    fun findByUsername(userName: String): Optional<User> {
        return userRepository.findByUserName(userName)
    }

    fun findById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun delete(user: User): User {
        return userRepository.delete(user)
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        log.info("Loading user $username")
        val user = userRepository.findByUserName(username)
        if (user.isEmpty) {
            throw UsernameNotFoundException("Not found")
        }

        return UserDetailsAdapter(user.get())
    }

    fun registerUser(registerRequest: RegisterRequest): ResponseEntity<*> {
        val checkExisting = findByUsername(registerRequest.email)
        if (checkExisting.isPresent) {
            return ResponseEntity("That email is already registered", HttpStatus.BAD_REQUEST)
        }
        if (!validateRequest(registerRequest)) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        // first registration will get admin role
        val auth = if (findAll().isEmpty()) "ADMIN" else "USER"

        save(
            User(
                userName = registerRequest.email,
                password = BCryptPasswordEncoder().encode(registerRequest.password),
                authority = auth,
                balance = BigDecimal.valueOf(100.00),
                itemsBought = mutableListOf()
            )
        )
        return ResponseEntity("Successfully registered", HttpStatus.OK)
    }

    fun getBalance(
        userDetails: UserDetailsAdapter
    ): ResponseEntity<*> {
        val user = findByUsername(userDetails.username).getOrNull()
            ?: return ResponseEntity(
                "User ${userDetails.username} not found",
                HttpStatus.NOT_FOUND
            )
        return ResponseEntity(
            "Current balance for ${user.userName}: ${user.balance}",
            HttpStatus.OK
        )
    }

    fun seeItemsBought(
        userDetails: UserDetailsAdapter
    ): ResponseEntity<*> {

        val user = findByUsername(userDetails.username).getOrNull()
            ?: return ResponseEntity(
                "User ${userDetails.username} not found",
                HttpStatus.NOT_FOUND
            )

        user.itemsBought.forEach {
            println(it)
        }

        return ResponseEntity(
            "Current items purchased by ${user.userName}: ${user.itemsBought}",
            HttpStatus.OK
        )
    }

    fun removeUser(id: Long
    ): ResponseEntity<*> {
        if (id == 1L) {
            return ResponseEntity(
                "Cannot delete admin",
                HttpStatus.BAD_REQUEST
            )
        }
        val userOptional = findById(id)
        if (userOptional.isEmpty) {
            return ResponseEntity(
                "User $id not found",
                HttpStatus.NOT_FOUND
            )
        }
        val user = userOptional.get()

        delete(user)
        return ResponseEntity(
            "User $id deleted",
            HttpStatus.OK
        )
    }

    fun validateRequest(registerRequest: RegisterRequest): Boolean {
        return validateEmail(registerRequest.email) && registerRequest.password.length >= 5
    }

    fun validateEmail(email: String): Boolean {
        val regex = Regex("^[^@]+@[^@.]+\\.[^@.]+$")
        return regex.matches(email)
    }
}

data class RegisterRequest(
    val email: String,
    val password: String
)