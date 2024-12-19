package com.example.nhom3_project

data class Bills(
    val id: String,
    var userid: String,
    val products: List<PayData> = emptyList(),
    var addressid: String,
    var paymentid: String,
    var totalpayment: Double,
    var creationdate: String
)
