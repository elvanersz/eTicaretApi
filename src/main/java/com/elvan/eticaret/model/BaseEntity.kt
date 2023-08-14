package com.elvan.eticaret.model

import java.time.LocalDateTime

data class BaseEntity (

        val createdDate: LocalDateTime? = null,
        val updatedDate: LocalDateTime? = null
){

}