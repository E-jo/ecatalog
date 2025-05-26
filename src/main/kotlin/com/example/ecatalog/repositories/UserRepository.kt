package com.example.ecatalog.repositories

import com.example.ecatalog.models.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : PagingAndSortingRepository<User, Int> {
    fun findAll(): List<User>
    fun findByUserName(userName: String): Optional<User>
    fun findById(id: Long): Optional<User>
    fun save(user: User): User
    fun delete(user: User): User
}