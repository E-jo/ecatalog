package com.example.ecatalog

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDateTime

@Component
class RestAuthenticationEntryPoint : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val status = response.status

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val body: MutableMap<String, Any> = HashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["status"] = HttpStatus.UNAUTHORIZED.value()
        body["error"] = HttpStatus.UNAUTHORIZED.reasonPhrase
        body["path"] = request.requestURI

        val objectMapper = ObjectMapper()
        objectMapper.writeValue(response.outputStream, body)

        response.sendError(status, authException.message)
    }
}