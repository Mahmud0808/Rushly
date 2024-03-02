package com.drdisagree.rushly.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address,
    val date: String = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.ENGLISH
    ).format(System.currentTimeMillis()),
    val orderId: Long = nextLong(
        0,
        100_000_000_000
    ) + System.currentTimeMillis() + UUID.randomUUID().hashCode(),
    val timestamp: Timestamp = Timestamp.now(),
    val userId: String = FirebaseAuth.getInstance().uid!!
) : Parcelable {
    constructor() : this("", 0f, emptyList(), Address())
}