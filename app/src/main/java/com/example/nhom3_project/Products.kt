package com.example.nhom3_project

data class Products(val id: String,
                    val name: String,
                    val price: Double,
                    var img: String,
                    var quantity: Int = 1,
                    var isSelected: Boolean = false)
