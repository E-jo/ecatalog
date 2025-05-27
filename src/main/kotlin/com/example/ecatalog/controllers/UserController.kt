package com.example.ecatalog.controllers

import com.example.ecatalog.models.UserDetailsAdapter
import com.example.ecatalog.services.RegisterRequest
import com.example.ecatalog.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) {

    @PostMapping(path = ["/users/register"])
    fun registerUser(
        @Valid @RequestBody registerRequest: RegisterRequest
    ): ResponseEntity<*> {
        return userService.registerUser(registerRequest)
    }

    @GetMapping(path = ["/users/balance"])
    fun getBalance(
        @AuthenticationPrincipal userDetails: UserDetailsAdapter
    ): ResponseEntity<*> {
        return userService.getBalance(userDetails)
    }

    @GetMapping(path = ["/users/items"])
    fun seeItemsBought(
        @AuthenticationPrincipal userDetails: UserDetailsAdapter
    ): ResponseEntity<*> {
        return userService.seeItemsBought(userDetails)
    }

    @PostMapping(path = ["/users/remove/{id}"])
    fun removeUser(
        @AuthenticationPrincipal userDetails: UserDetailsAdapter,
        @PathVariable id: Long
    ): ResponseEntity<*> {
        return userService.removeUser(id)
    }

    @GetMapping(path = ["/users"])
    fun allUsers(
        @AuthenticationPrincipal userDetails: UserDetailsAdapter
    ): ResponseEntity<*> {
        return ResponseEntity(
            userService.findAll(),
            HttpStatus.OK
        )
    }
}

