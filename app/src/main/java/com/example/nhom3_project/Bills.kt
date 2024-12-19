package com.example.nhom3_project

import java.util.Date

data class Bills(
    val id: String,
    var cartitemid: String,
    var userid: String,
    var addressid: String,
    var paymentid: String,
    var totalpayment: Int,
    var creationdate: Date
)
