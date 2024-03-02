package com.drdisagree.rushly.ui.fragments.adminpanel

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.drdisagree.rushly.R
import com.drdisagree.rushly.databinding.FragmentNewProductBinding
import com.drdisagree.rushly.ui.viewmodels.NewProductViewModel
import com.drdisagree.rushly.utils.Resource
import com.drdisagree.rushly.utils.validations.NewProductValidation
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class NewProductFragment : Fragment() {

    private lateinit var binding: FragmentNewProductBinding
    private val viewModel by viewModels<NewProductViewModel>()
    private var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val categories = resources.getStringArray(R.array.product_categories)
            spinnerCategory.adapter = ArrayAdapter(
                requireContext(),
                R.layout.new_product_spinner_item,
                categories
            )

            imageNewProductClose.setOnClickListener {
                findNavController().navigateUp()
            }

            layoutColorPicker.setOnClickListener {
                ColorPickerDialog.Builder(requireContext())
                    .setTitle("Product color")
                    .setPositiveButton("Select", object : ColorEnvelopeListener {
                        override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                            envelope?.let {
                                selectedColors.add(it.color)
                                updateColors()
                            }
                        }
                    })
                    .setNegativeButton("Cancel") { colorPicker, _ ->
                        colorPicker.dismiss()
                    }
                    .attachAlphaSlideBar(false)
                    .show()
            }

            layoutImagePicker.setOnLongClickListener {
                selectedColors.clear()
                tvSelectedColors.text = getString(R.string.none)
                true
            }

            val selectImagesActivityResult = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    // Multiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData!!.itemCount
                        (0 until count).forEach { i ->
                            val imageUri = intent.clipData?.getItemAt(i)?.uri
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }

                    updateImages()
                }
            }

            layoutImagePicker.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                selectImagesActivityResult.launch(intent)
            }

            layoutImagePicker.setOnLongClickListener {
                selectedImages.clear()
                tvSelectedImages.text = getString(R.string.none)
                true
            }

            layoutSpecialProduct.setOnClickListener {
                checkBoxSpecialProduct.isChecked = !checkBoxSpecialProduct.isChecked
            }

            buttonAddProduct.setOnClickListener {
                val id = UUID.randomUUID().toString()
                val name = edName.text.toString().trim()
                val category = spinnerCategory.selectedItem.toString().trim()
                val price = edPrice.text.toString().trim()
                val offerPercentage = edOfferPercentage.text.toString().trim()
                val description = edDescription.text.toString().trim()
                val sizes = getSizesAsList(edSizes.text.toString().trim())
                val special = checkBoxSpecialProduct.isChecked
                val imagesByteArrays = getImagesByteArrays()

                viewModel.saveNewProduct(
                    id = id,
                    name = name,
                    category = category,
                    price = price,
                    offerPercentage = if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                    description = description.ifEmpty { null },
                    colors = selectedColors.toList(),
                    sizes = sizes,
                    special = special,
                    imagesByteArray = imagesByteArrays
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.addProduct.collect {
                    when (it) {
                        is Resource.Loading -> {
                            buttonAddProduct.startAnimation()
                        }

                        is Resource.Success -> {
                            buttonAddProduct.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Product added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            resetAll()
                        }

                        is Resource.Error -> {
                            buttonAddProduct.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.validation.collect {
                    if (it.name is NewProductValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            edName.apply {
                                requestFocus()
                                error = it.name.error
                            }
                        }
                    }
                    if (it.category is NewProductValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            spinnerCategory.apply {
                                requestFocus()
                            }
                            Toast.makeText(
                                requireContext(),
                                it.category.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    if (it.price is NewProductValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            edPrice.apply {
                                requestFocus()
                                error = it.price.error
                            }
                        }
                    }
                    if (it.offerPercentage is NewProductValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            edOfferPercentage.apply {
                                requestFocus()
                                error = it.offerPercentage.error
                            }
                        }
                    }
                    if (it.images is NewProductValidation.Invalid) {
                        withContext(Dispatchers.Main) {
                            buttonImagePicker.apply {
                                requestFocus()
                                error = it.images.error
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSizesAsList(sizesStr: String): List<String>? {
        return if (sizesStr.isEmpty()) null else sizesStr.split(",")
    }

    private fun updateColors() {
        var colors = ""
        var firstItem = true

        selectedColors.forEach {
            colors = if (firstItem)
                "#${Integer.toHexString(it).substring(2)}"
            else
                "$colors, #${Integer.toHexString(it).substring(2)}"

            firstItem = false
        }

        binding.tvSelectedColors.text = colors.uppercase(Locale.ROOT)
    }

    private fun updateImages() {
        val count = selectedImages.size
        val text = if (count <= 1) "Selected $count image" else "Selected $count images"
        binding.tvSelectedImages.text = text
    }

    @Suppress("deprecation")
    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()

        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            } else {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(requireContext().contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            if (imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }

        return imagesByteArray
    }

    private fun resetAll() {
        binding.apply {
            edName.text.clear()
            spinnerCategory.setSelection(0)
            edDescription.text.clear()
            edPrice.text.clear()
            edOfferPercentage.text.clear()
            edSizes.text.clear()
            checkBoxSpecialProduct.isChecked = false
            tvSelectedColors.text = getString(R.string.none)
            tvSelectedImages.text = getString(R.string.none)
            selectedColors.clear()
            selectedImages.clear()
        }
    }
}