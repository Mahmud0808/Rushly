package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.PagingInfo
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val allProducts = _allProducts.asStateFlow()

    private val productsPagingInfo = PagingInfo()

    private var productList = mutableListOf<Product>()

    init {
        fetchProducts()
    }

    fun fetchProducts(query: String? = null) {
        viewModelScope.launch {
            _allProducts.emit(Resource.Loading())
        }

        if (!query.isNullOrEmpty()) {
            productsPagingInfo.pageCount = 1
            productsPagingInfo.isPagingEnd = false
            productsPagingInfo.oldItemList = emptyList()
        }

        if (productsPagingInfo.isPagingEnd) return

        if (query.isNullOrEmpty()) {
            if (productList.isEmpty()) {
                firestore.collection(PRODUCT_COLLECTION)
                    .limit(productsPagingInfo.pageCount * 10)
                    .get()
                    .addOnSuccessListener {
                        val products = it.toObjects(Product::class.java)
                        productsPagingInfo.isPagingEnd =
                            products == productsPagingInfo.oldItemList
                        productsPagingInfo.oldItemList = products

                        viewModelScope.launch {
                            _allProducts.emit(Resource.Success(products))
                        }

                        productsPagingInfo.pageCount++
                    }
                    .addOnFailureListener {
                        viewModelScope.launch {
                            _allProducts.emit(Resource.Error(it.message.toString()))
                        }
                    }
            } else {
                viewModelScope.launch {
                    _allProducts.emit(
                        Resource.Success(
                            productList.take((productsPagingInfo.pageCount * 10).toInt())
                        )
                    )
                }
            }
        } else {
            if (productList.isEmpty()) {
                firestore.collection(PRODUCT_COLLECTION)
                    .get()
                    .addOnSuccessListener {
                        productList = it.toObjects(Product::class.java)

                        val queryProducts = mutableListOf<Product>()
                        for (product in productList) {
                            if (product.name.lowercase().contains(query.lowercase())) {
                                queryProducts.add(product)
                            }
                        }

                        viewModelScope.launch {
                            _allProducts.emit(Resource.Success(queryProducts))
                        }
                    }
                    .addOnFailureListener {
                        viewModelScope.launch {
                            _allProducts.emit(Resource.Error(it.message.toString()))
                        }
                    }
            } else {
                val queryProducts = mutableListOf<Product>()
                for (product in productList) {
                    if (product.name.lowercase().contains(query.lowercase())) {
                        queryProducts.add(product)
                    }

                    viewModelScope.launch {
                        _allProducts.emit(Resource.Success(queryProducts))
                    }
                }
            }
        }
    }
}