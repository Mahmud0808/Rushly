package com.drdisagree.rushly.data

sealed class Category(
    val category: String
) {
    data object Chair : Category("Chair")
    data object Cupboard : Category("Cupboard")
    data object Table : Category("Table")
    data object Accessory : Category("Accessory")
    data object Furniture : Category("Furniture")
}