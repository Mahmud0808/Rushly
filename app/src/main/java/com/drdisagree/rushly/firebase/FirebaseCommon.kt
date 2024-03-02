package com.drdisagree.rushly.firebase

import com.drdisagree.rushly.data.CartProduct
import com.drdisagree.rushly.utils.Constants.CART_COLLECTION
import com.drdisagree.rushly.utils.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val cartCollection = firestore.collection(USER_COLLECTION)
        .document(firebaseAuth.uid!!)
        .collection(CART_COLLECTION)

    fun addProductToCart(
        cartProduct: CartProduct,
        onResult: (CartProduct?, Exception?) -> Unit
    ) {
        cartCollection.document()
            .set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(
        documentId: String,
        onResult: (String?, Exception?) -> Unit
    ) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val productObject = transaction.get(documentRef).toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun decreaseQuantity(
        documentId: String,
        onResult: (String?, Exception?) -> Unit
    ) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val productObject = transaction.get(documentRef).toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    enum class QuantityChanging {
        INCREASE,
        DECREASE
    }
}