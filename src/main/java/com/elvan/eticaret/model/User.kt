package com.elvan.eticaret.model

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

//@Entity  3.tablo oluştuğu için silindi
data class User(

        @field:Id
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        val mail: String,
        val firstName: String,
        val lastName: String,
        val middleName: String
)