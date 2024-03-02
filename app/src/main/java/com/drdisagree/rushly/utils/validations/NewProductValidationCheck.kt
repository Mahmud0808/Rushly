package com.drdisagree.rushly.utils.validations

fun validateNewProductName(name: String): NewProductValidation {
    return if (name.isEmpty()) {
        NewProductValidation.Invalid("Name cannot be empty")
    } else {
        NewProductValidation.Valid
    }
}

fun validateNewProductCategory(category: String): NewProductValidation {
    return if (category.isEmpty() or (category == "Category")) {
        NewProductValidation.Invalid("Category cannot be empty")
    } else {
        NewProductValidation.Valid
    }
}

fun validateNewProductPrice(price: String): NewProductValidation {
    return if (price.isEmpty()) {
        NewProductValidation.Invalid("Price cannot be empty")
    } else if (price.toFloat() < 0) {
        NewProductValidation.Invalid("Price cannot be negative")
    } else {
        NewProductValidation.Valid
    }
}

fun validateNewProductOfferPercentage(offerPercentage: Float? = null): NewProductValidation {
    return if (offerPercentage != null && (offerPercentage < 0 || offerPercentage > 100)) {
        NewProductValidation.Invalid("Invalid offer percentage")
    } else {
        NewProductValidation.Valid
    }
}

fun validateNewProductImages(imagesByteArray: List<ByteArray>?): NewProductValidation {
    return if (imagesByteArray.isNullOrEmpty()) {
        NewProductValidation.Invalid("Provide at least one image")
    } else {
        NewProductValidation.Valid
    }
}