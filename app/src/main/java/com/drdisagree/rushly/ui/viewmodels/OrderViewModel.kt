package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.Order
import com.drdisagree.rushly.utils.Constants.CART_COLLECTION
import com.drdisagree.rushly.utils.Constants.ORDER_COLLECTION
import com.drdisagree.rushly.utils.Constants.USER_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        firestore.runBatch {
            firestore.collection(USER_COLLECTION)
                .document(firebaseAuth.uid!!)
                .collection(ORDER_COLLECTION)
                .document()
                .set(order)

            firestore.collection(ORDER_COLLECTION)
                .document()
                .set(order)

            firestore.collection(USER_COLLECTION)
                .document(firebaseAuth.uid!!)
                .collection(CART_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach {
                        it.reference.delete()
                    }
                }
        }
            .addOnSuccessListener {
                viewModelScope.launch {
                    _order.emit(Resource.Success(order))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _order.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateOrderStatus(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        firestore.runTransaction { transaction ->
            val query = firestore.collection(ORDER_COLLECTION)
                .whereEqualTo("orderId", order.orderId)
                .get()

            val querySnapshots = Tasks.await(query)

            if (!querySnapshots.isEmpty) {
                val querySnapshot = querySnapshots.documents[0]
                val documentRef = querySnapshot.reference
                transaction.set(documentRef, order)
            }

            val query2 = firestore.collection(USER_COLLECTION)
                .document(order.userId)
                .collection(ORDER_COLLECTION)
                .whereEqualTo("orderId", order.orderId)
                .get()

            val querySnapshots2 = Tasks.await(query2)

            if (!querySnapshots2.isEmpty) {
                val querySnapshot = querySnapshots2.documents[0]
                val documentRef = querySnapshot.reference
                transaction.set(documentRef, order)
            }
        }
            .addOnSuccessListener {
                viewModelScope.launch {
                    _order.emit(Resource.Success(order))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _order.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}