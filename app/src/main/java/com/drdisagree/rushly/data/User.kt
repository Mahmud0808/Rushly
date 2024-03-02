package com.drdisagree.rushly.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val imagePath: String = ""
) : Parcelable {
    constructor() : this("", "", "", "")
}