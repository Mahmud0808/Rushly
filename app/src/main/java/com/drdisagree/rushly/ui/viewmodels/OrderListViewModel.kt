package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.Order
import com.drdisagree.rushly.utils.Constants.ORDER_COLLECTION
import com.drdisagree.rushly.utils.Constants.USER_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _myOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val myOrders = _myOrders.asStateFlow()

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    init {
        getMyOrders()
        getAllOrders()
    }

    private fun getMyOrders() {
        viewModelScope.launch {
            _myOrders.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _myOrders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _myOrders.emit(Resource.Error(it.message.toString()))
                }
            }

        firestore.collection(USER_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _myOrders.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    value?.let {
                        viewModelScope.launch {
                            _myOrders.emit(Resource.Success(it.toObjects(Order::class.java)))
                        }
                    }
                }
            }
    }

    private fun getAllOrders() {
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        firestore.collection(ORDER_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error(it.message.toString()))
                }
            }

        firestore.collection(ORDER_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    value?.let {
                        viewModelScope.launch {
                            _allOrders.emit(Resource.Success(it.toObjects(Order::class.java)))
                        }
                    }
                }
            }
    }
}