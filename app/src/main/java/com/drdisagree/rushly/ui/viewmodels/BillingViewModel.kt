package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.Address
import com.drdisagree.rushly.utils.Constants.ADDRESS_COLLECTION
import com.drdisagree.rushly.utils.Constants.USER_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddresses()
    }

    private fun getUserAddresses() {
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _address.emit(Resource.Error(error.message.toString()))
                    }
                    return@addSnapshotListener
                }

                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch {
                    _address.emit(Resource.Success(addresses!!))
                }
            }
    }
}