package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.PagingInfo
import com.drdisagree.rushly.data.Product
import com.drdisagree.rushly.utils.Constants.PRODUCT_COLLECTION
import com.drdisagree.rushly.utils.Constants.PRODUCT_SPECIAL_FIELD
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: MutableStateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals: MutableStateFlow<Resource<List<Product>>> = _bestDeals

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: MutableStateFlow<Resource<List<Product>>> = _bestProducts

    val specialProductsPagingInfo = PagingInfo()
    val bestDealsPagingInfo = PagingInfo()
    val bestProductsPagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()
    }

    fun fetchSpecialProducts() {
        if (specialProductsPagingInfo.isPagingEnd) return

        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCT_COLLECTION)
            .whereEqualTo(PRODUCT_SPECIAL_FIELD, true)
            .limit(specialProductsPagingInfo.pageCount * 10)
            .get()
            .addOnSuccessListener {
                val specialProductsList = it.toObjects(Product::class.java)
                specialProductsPagingInfo.isPagingEnd =
                    specialProductsList == specialProductsPagingInfo.oldItemList
                specialProductsPagingInfo.oldItemList = specialProductsList

                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }

                specialProductsPagingInfo.pageCount++
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDealsProducts() {
        if (bestDealsPagingInfo.isPagingEnd) return

        viewModelScope.launch {
            _bestDeals.emit(Resource.Loading())
        }

        firestore.collection(PRODUCT_COLLECTION)
            .whereNotEqualTo("offerPercentage", null)
            .orderBy("offerPercentage", Query.Direction.DESCENDING)
            .limit(bestDealsPagingInfo.pageCount * 10)
            .get()
            .addOnSuccessListener {
                val bestDealsProductsList = it.toObjects(Product::class.java)
                bestDealsPagingInfo.isPagingEnd =
                    bestDealsProductsList == bestDealsPagingInfo.oldItemList
                bestDealsPagingInfo.oldItemList = bestDealsProductsList

                viewModelScope.launch {
                    _bestDeals.emit(Resource.Success(bestDealsProductsList))
                }

                bestDealsPagingInfo.pageCount++
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (bestProductsPagingInfo.isPagingEnd) return

        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        firestore.collection(PRODUCT_COLLECTION)
            .limit(bestProductsPagingInfo.pageCount * 10)
            .get()
            .addOnSuccessListener {
                val bestProductsList = it.toObjects(Product::class.java)
                bestProductsPagingInfo.isPagingEnd =
                    bestProductsList == bestProductsPagingInfo.oldItemList
                bestProductsPagingInfo.oldItemList = bestProductsList

                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(bestProductsList))
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