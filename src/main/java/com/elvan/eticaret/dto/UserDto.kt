package com.elvan.eticaret.dto

data class UserDto (
        val mail: String,
        val firstName: String,
        val lastName: String,
        val middleName: String,
        val userDetails: List<UserDetailsDto>
)