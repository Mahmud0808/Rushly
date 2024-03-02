package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.Category
import com.drdisagree.rushly.data.PagingInfo
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.utils.Constants.PRODUCT_CATEGORY_FIELD
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    val offerProductsPagingInfo = PagingInfo()
    val bestProductsPagingInfo = PagingInfo()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        if (offerProductsPagingInfo.isPagingEnd) return

        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCT_COLLECTION)
            .whereEqualTo(PRODUCT_CATEGORY_FIELD, category.category)
            .whereNotEqualTo("offerPercentage", null)
            .limit(offerProductsPagingInfo.pageCount * 10)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                offerProductsPagingInfo.isPagingEnd =
                    products == offerProductsPagingInfo.oldItemList
                offerProductsPagingInfo.oldItemList = products

                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }

                offerProductsPagingInfo.pageCount++
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (bestProductsPagingInfo.isPagingEnd) return

        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCT_COLLECTION)
            .whereEqualTo(PRODUCT_CATEGORY_FIELD, category.category)
            .limit(bestProductsPagingInfo.pageCount * 10)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                bestProductsPagingInfo.isPagingEnd =
                    products == bestProductsPagingInfo.oldItemList
                bestProductsPagingInfo.oldItemList = products

                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))
                }

                bestProductsPagingInfo.pageCount++
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}