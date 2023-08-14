package com.elvan.eticaret.dto

data class CreateUserDetailsRequest(
        val phoneNumber:String,
        val address: String,
        val city: String,
        val country: String,
        val postCode: String,
        val userId: Long
)

data class UpdateUserDetailsRequest(
        val phoneNumber:String,
        val address: String,
        val city: String,
        val country: String,
        val postCode: String
)