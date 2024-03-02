package com.drdisagree.rushly.data

sealed class OrderStatus(val status: String) {

    data object Pending : OrderStatus("Pending")
    data object Confirmed : OrderStatus("Confirmed")
    data object Shipped : OrderStatus("Shipped")
    data object Delivered : OrderStatus("Delivered")
    data object Canceled : OrderStatus("Canceled")
    data object Returned : OrderStatus("Returned")
}

fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Pending" -> {
            OrderStatus.Pending
        }

        "Confirmed" -> {
            OrderStatus.Confirmed
        }

        "Shipped" -> {
            OrderStatus.Shipped
        }

        "Delivered" -> {
            OrderStatus.Delivered
        }

        "Canceled" -> {
            OrderStatus.Canceled
        }

        "Returned" -> {
            OrderStatus.Returned
        }

        else -> {
            OrderStatus.Pending
        }
    }
}