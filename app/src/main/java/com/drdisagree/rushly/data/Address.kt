package com.drdisagree.rushly.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val label: String,
    val address: String,
    val street: String,
    val city: String,
    val state: String,
    val phone: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}
