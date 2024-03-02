package com.drdisagree.rushly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.rushly.data.Address
import com.drdisagree.rushly.utils.Constants.ADDRESS_COLLECTION
import com.drdisagree.rushly.utils.Constants.USER_COLLECTION
import com.drdisagree.rushly.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _getAddresses = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val getAddresses = _getAddresses.asStateFlow()

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private var addressDocument = emptyList<DocumentSnapshot>()

    init {
        getAddresses()
    }

    private fun getAddresses() {
        viewModelScope.launch {
            _getAddresses.emit(Resource.Loading())
        }

        firestore.collection(USER_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _getAddresses.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    addressDocument = value.documents
                    val addressDocuments = value.toObjects(Address::class.java)
                    viewModelScope.launch {
                        _getAddresses.emit(Resource.Success(addressDocuments))
                    }
                }
            }
    }

    fun addAddress(address: Address) {
        if (validateInputs(address)) {
            viewModelScope.launch {
                _address.emit(Resource.Loading())
            }

            firestore.collection(USER_COLLECTION)
                .document(firebaseAuth.uid!!)
                .collection(ADDRESS_COLLECTION)
                .document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Success(listOf(address)))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
        }
    }

    fun deleteAddress(address: Address) {
        val index = _getAddresses.value.data?.indexOf(address)

        if (index != null && index != -1) {
            val documentId = addressDocument[index].id
            firestore.collection(USER_COLLECTION)
                .document(firebaseAuth.uid!!)
                .collection(ADDRESS_COLLECTION)
                .document(documentId)
                .delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Success(listOf(address)))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.label.trim().isNotEmpty() &&
                address.address.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty()

    }
}