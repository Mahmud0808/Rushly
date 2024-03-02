package com.drdisagree.rushly.utils.validations

sealed class NewProductValidation {
    data object Valid : NewProductValidation()
    data class Invalid(val error: String) : NewProductValidation()
}

data class NewProductFieldsState(
    val name: NewProductValidation,
    val category: NewProductValidation,
    val price: NewProductValidation,
    val offerPercentage: NewProductValidation,
    val images: NewProductValidation
)