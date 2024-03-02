package com.drdisagree.rushly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.validations.NewProductFieldsState
import com.drdisagree.rushly.utils.validations.NewProductValidation
import com.drdisagree.rushly.utils.validations.validateNewProductCategory
import com.drdisagree.rushly.utils.validations.validateNewProductImages
import com.drdisagree.rushly.utils.validations.validateNewProductName
import com.drdisagree.rushly.utils.validations.validateNewProductOfferPercentage
import com.drdisagree.rushly.utils.validations.validateNewProductPrice
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewProductViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference
) : ViewModel() {

    private val tag = this.javaClass.simpleName

    private val _addProduct = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val addProduct: Flow<Resource<Product>> = _addProduct

    private val _validation = Channel<NewProductFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun saveNewProduct(
        id: String,
        name: String,
        category: String,
        price: String,
        offerPercentage: Float? = null,
        description: String? = null,
        colors: List<Int>? = null,
        sizes: List<String>? = null,
        special: Boolean = false,
        imagesByteArray: List<ByteArray>?
    ) {
        if (checkValidation(
                name,
                category,
                price,
                offerPercentage,
                imagesByteArray
            )
        ) {
            runBlocking {
                _addProduct.emit(Resource.Loading())
            }

            imagesByteArray?.let {
                uploadImagesAndSaveProduct(
                    id,
                    name,
                    category,
                    price.toFloat(),
                    offerPercentage,
                    description,
                    colors,
                    sizes,
                    special,
                    it
                )
            }
        } else {
            runBlocking {
                _validation.send(
                    NewProductFieldsState(
                        validateNewProductName(name),
                        validateNewProductCategory(category),
                        validateNewProductPrice(price),
                        validateNewProductOfferPercentage(offerPercentage),
                        validateNewProductImages(imagesByteArray)
                    )
                )
            }
        }
    }

    private fun checkValidation(
        name: String,
        category: String,
        price: String,
        offerPercentage: Float? = null,
        imagesByteArray: List<ByteArray>?
    ): Boolean {
        val nameValidation = validateNewProductName(name)
        val categoryValidation = validateNewProductCategory(category)
        val priceValidation = validateNewProductPrice(price)
        val offerPercentageValidation = validateNewProductOfferPercentage(offerPercentage)
        val imagesValidation = validateNewProductImages(imagesByteArray)

        return nameValidation is NewProductValidation.Valid &&
                categoryValidation is NewProductValidation.Valid &&
                priceValidation is NewProductValidation.Valid &&
                offerPercentageValidation is NewProductValidation.Valid &&
                imagesValidation is NewProductValidation.Valid
    }

    private fun uploadImagesAndSaveProduct(
        id: String,
        name: String,
        category: String,
        price: Float,
        offerPercentage: Float? = null,
        description: String? = null,
        colors: List<Int>? = null,
        sizes: List<String>? = null,
        special: Boolean = false,
        imagesByteArray: List<ByteArray>
    ) {
        val images = mutableListOf<String>()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    async {
                        imagesByteArray.forEach {
                            launch {
                                val imageId = UUID.randomUUID().toString()
                                val imageStorage = storage.child(PRODUCT_COLLECTION)
                                    .child("images")
                                    .child(imageId)
                                val result = imageStorage.putBytes(it).await()
                                val downloadUrl = result.storage.downloadUrl.await().toString()
                                images.add(downloadUrl)
                            }
                        }
                    }.await()
                } catch (e: Exception) {
                    _addProduct.value = Resource.Error("Failed to upload images")
                    Log.e(tag, e.message.toString())
                    cancel(message = e.message.toString())
                }

                val product = Product(
                    id = id,
                    name = name,
                    category = category,
                    price = price,
                    offerPercentage = offerPercentage,
                    description = description,
                    colors = colors,
                    sizes = sizes,
                    images = images,
                    special = special
                )

                Log.d(tag, product.toString())

                firestore.collection(PRODUCT_COLLECTION).add(product)
                    .addOnSuccessListener {
                        _addProduct.value = Resource.Success(product)
                    }
                    .addOnFailureListener {
                        _addProduct.value = Resource.Error(it.message.toString())
                    }
            }
        }
    }
}