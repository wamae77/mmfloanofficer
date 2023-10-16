package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.ViewDocAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentViewDocumentBinding
import com.deefrent.rnd.fieldapp.dtos.DocDTO
import com.deefrent.rnd.fieldapp.network.models.DocumentData
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.ViewDocumentCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel
import com.deefrent.rnd.fieldapp.viewModels.DocumentViewModel
import com.github.chrisbanes.photoview.PhotoView
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult

class ViewDocumentFragment : Fragment(), ViewDocumentCallBack {
    lateinit var imagePicker: ImagePicker
    private var _binding: FragmentViewDocumentBinding? = null
    private val binding get() = _binding!!
    private lateinit var docAdapter: ViewDocAdapter
    var scrollingDown = false

    //private val args1: ViewDocumentFragmentArgs by navArgs()
    private val sharedViewModel by lazy {
        ViewModelProvider(requireActivity())[DocumentViewModel::class.java]
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[CustomerAssessmentHomeViewModel::class.java]
    }
    private lateinit var docDto: DocDTO
    private var spanCount = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewDocumentBinding.inflate(inflater)
        docDto = DocDTO()
        viewModel.iDLookUpData.observe(viewLifecycleOwner) {
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            docDto.customerIdNumber = it?.idNumber.toString()
            getCustomerDoc()
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
            //binding.tvAppBarTitle.text = args.docTypeName
        }
        binding.fabAddDocument.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupListeners()
        setupAdapter()
        sharedViewModel.docData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                docAdapter.swapData(it)
                binding.rvViewDoc.makeVisible()
                binding.tvNoRequest.makeGone()
            } else {
                binding.rvViewDoc.makeGone()
                binding.LinearLayoutError.makeGone()
                binding.tvNoRequest.makeVisible()
            }
        }
        //docDto.docTypeId = args.docId
        binding.btnRefresh.setOnClickListener {
            getCustomerDoc()
        }
        binding.svAssessment.setOnRefreshListener {
            getCustomerDoc()
        }
        binding.fabAddDocument.setOnClickListener {
            findNavController().navigate(R.id.action_viewDocumentsFragment_to_addDocumentFragment)
        }
    }

    private fun getCustomerDoc() {
        if (isNetworkAvailable(requireContext())) {
            binding.LinearLayoutError.makeGone()
            binding.tvNoRequest.makeGone()
            sharedViewModel.getCustomerDoc(docDto)
        } else {
            binding.tvNoRequest.makeGone()
            binding.rvViewDoc.makeGone()
            binding.tvError.text = "Check your internet connection and try again"
            binding.LinearLayoutError.makeVisible()
        }
    }

    private fun setupViews() {
        sharedViewModel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.tvNoRequest.makeGone()
                        binding.rvViewDoc.makeGone()
                        binding.LinearLayoutError.makeGone()
                        binding.progressr.mainPBar.makeVisible()
                        binding.progressr.tvWait.text = "Fetching customer documents..."
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.progressr.mainPBar.makeGone()
                        binding.svAssessment.isRefreshing = false
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.progressr.mainPBar.makeGone()
                        binding.svAssessment.isRefreshing = false
                    }
                }
            }
        }
        viewModel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        viewModel.stopObserving()
                    }
                    0 -> {
                        viewModel.stopObserving()
                        binding.LinearLayoutError.makeVisible()
                        binding.rvViewDoc.makeGone()
                        binding.tvNoRequest.makeGone()
                    }
                    else -> {
                        viewModel.stopObserving()
                        binding.LinearLayoutError.makeVisible()
                        binding.rvViewDoc.makeGone()
                        binding.tvNoRequest.makeGone()
                    }
                }
            }
        }

    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.rvViewDoc.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (scrollingDown && dy >= 0) {
                    scrollingDown = !scrollingDown
                    binding.fabAddDocument.hide()
                } else if (!scrollingDown && dy < 0) {
                    scrollingDown = !scrollingDown
                    binding.fabAddDocument.show()
                }
            }
        })
    }

    private fun setupAdapter() {
        docAdapter = ViewDocAdapter(this,this@ViewDocumentFragment)

        binding.rvViewDoc.apply {
            adapter = docAdapter
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            addItemDecoration(GridSpacingItemDecoration(spanCount, 0, false))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)

    }

    override fun onItemSelected(pos: Int, listItems: DocumentData, imageView: ImageView) {
        val image = (imageView.drawable as BitmapDrawable).bitmap
        val mBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.WrapContentDialog)
        val mView: View =
            layoutInflater.inflate(R.layout.preview_image, null)
        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
        ivImagePreview.setImageBitmap(image)
        /*Glide.with(requireActivity()).load(listItems.url)
            .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
            .into(ivImagePreview)*/
        mBuilder.setView(mView)
        val mDialog: AlertDialog = mBuilder.create()
        mDialog.show()
    }

    override fun onEditDocument(
        pos: Int,
        listItems: DocumentData,
        textView: TextView,
        imageView: ImageView
    ) {
        showPickerOptionsDialog(textView, imageView)
    }

    private fun showPickerOptionsDialog(textView: TextView, imageView: ImageView) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    dialog.dismiss()
                    selectFromCamera(textView, imageView)
                }
                options[item] == "Choose From Gallery" -> {
                    dialog.dismiss()
                    selectFromGallery(textView, imageView)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun selectFromCamera(textView: TextView, imageView: ImageView) {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, "Camera", textView, imageView)
        }
    }

    private fun selectFromGallery(textView: TextView, imageView: ImageView) {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(
                imageResult, "Gallery", textView, imageView
            )
        }
    }

    private fun imageCallBack(
        imageResult: ImageResult<Uri>,
        from: String,
        textView: TextView,
        imageView: ImageView
    ) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                textView.text = "Upload"
                imageView.setImageURI(uri)
            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

}