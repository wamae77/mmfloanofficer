package com.deefrent.rnd.fieldapp.view.homepage.mileage

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.MileageAdapter
import com.deefrent.rnd.fieldapp.databinding.AddMileageDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditMileageDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentMileageBinding
import com.deefrent.rnd.fieldapp.dtos.AddMileageDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateMileageDTO
import com.deefrent.rnd.fieldapp.models.mileage.Mileage
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.MileageCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.MileageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class MileageFragment : Fragment(), MileageCallBack {
    private lateinit var binding: FragmentMileageBinding
    private lateinit var addMileageDialogBinding: AddMileageDialogBinding
    private lateinit var editMileageDialogBinding: EditMileageDialogBinding
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(MileageViewModel::class.java)
    }
    private lateinit var diaryAdapter: MileageAdapter
    private var arrayList: ArrayList<Mileage> = arrayListOf()
    private var displayList: ArrayList<Mileage> = arrayListOf()
    private lateinit var calendar: Calendar
    private var engineOil = MutableLiveData(0)
    private var tyrePressure = MutableLiveData(0)
    private var breakLight = MutableLiveData(0)
    private var carJack = MutableLiveData(0)
    private var breakDownSign = MutableLiveData(0)
    private var water = MutableLiveData(0)
    var scrollingDown = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMileageBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        diaryAdapter = MileageAdapter(displayList, this)
        binding.rvMileage.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvMileage.adapter = diaryAdapter
        viewmodel.getMileage()
        getMileage()
        binding.btnRefresh.setOnClickListener {
            viewmodel.getMileage()
        }
        binding.rvMileage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (scrollingDown && dy >= 0) {
                    scrollingDown = !scrollingDown
                    binding.btnAddMileage.hide()
                } else if (!scrollingDown && dy < 0) {
                    scrollingDown = !scrollingDown
                    binding.btnAddMileage.show()
                }
            }
        })
        return binding.root
    }

    private fun getMileage() {
        if (isNetworkAvailable(requireContext())) {
            binding.rvMileage.makeGone()
            binding.noMileage.makeGone()
            binding.btnAddMileage.makeGone()
            binding.noNewRequest.makeGone()
            binding.btnRefresh.makeGone()
            observeViewModel()
        } else {
            binding.rvMileage.makeGone()
            binding.noMileage.makeGone()
            binding.btnAddMileage.makeGone()
            binding.noNewRequest.makeVisible()
            binding.btnRefresh.makeVisible()
            binding.noNewRequest.text = "Please check your internet connection and try again!"
        }
    }

    private fun observeViewModel() {
        binding.rvMileage.makeGone()
        binding.noNewRequest.makeGone()
        binding.btnRefresh.makeGone()
        viewmodel.diaryData.observe(viewLifecycleOwner) {
            displayList.clear()
            displayList.addAll(it.data)
            diaryAdapter.notifyDataSetChanged()
            if (it.data.isEmpty()) {
                binding.rvMileage.makeVisible()
                binding.noMileage.makeVisible()
                binding.btnAddMileage.makeVisible()
                binding.noNewRequest.makeGone()
                binding.noNewRequest.text = "You have not added anything to your mileage"
                binding.btnRefresh.makeGone()
            } else {
                binding.noNewRequest.makeGone()
                binding.noMileage.makeGone()
                binding.rvMileage.makeVisible()
                binding.btnAddMileage.makeVisible()
                binding.btnRefresh.makeGone()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddMileage.setOnClickListener { addMileage() }
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.rvMileage.makeGone()
                        binding.btnAddMileage.makeGone()
                        binding.noNewRequest.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Fetching your mileage..."
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.rvMileage.makeVisible()
                        binding.btnAddMileage.makeVisible()
                        binding.noNewRequest.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeGone()
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.rvMileage.makeGone()
                        binding.btnAddMileage.makeGone()
                        binding.noNewRequest.makeVisible()
                        binding.noNewRequest.text =
                            "Oops! Error occurred while fetching the diary, please try again later"
                        binding.btnRefresh.makeVisible()
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        viewmodel.diaryData.observe(viewLifecycleOwner) { diary ->
                            displayList.clear()
                            displayList.addAll(diary.data)
                            diaryAdapter.notifyDataSetChanged()
                            if (diary.data.isEmpty()) {
                                binding.noMileage.makeVisible()
                                binding.rvMileage.makeGone()
                                binding.btnAddMileage.makeVisible()
                                binding.noNewRequest.makeGone()
                                binding.noNewRequest.text =
                                    "You have not added anything to your mileage"
                                binding.btnRefresh.makeGone()
                            } else {
                                binding.noMileage.makeGone()
                                binding.noNewRequest.makeGone()
                                binding.rvMileage.makeVisible()
                                binding.btnAddMileage.makeVisible()
                                binding.btnRefresh.makeGone()
                            }
                        }
                        viewmodel.stopObserving()
                    }
                    0 -> {
                        viewmodel.stopObserving()
                        binding.noNewRequest.text = viewmodel.statusMessage.value
                        binding.noNewRequest.makeVisible()
                        binding.btnRefresh.makeVisible()
                        binding.rvMileage.makeGone()
                        binding.btnAddMileage.makeGone()
                        binding.noMileage.makeGone()
                    }
                    else -> {
                        viewmodel.stopObserving()
                        binding.rvMileage.makeGone()
                        binding.btnAddMileage.makeGone()
                        binding.noMileage.makeGone()
                        binding.noNewRequest.makeVisible()
                        binding.noNewRequest.text =
                            "Oops! Error occurred while fetching the diary, please try again later"
                        binding.btnRefresh.makeVisible()
                        //onInfoDialog(getString(R.string.error_occurred))
                    }
                }
            }
        }
    }

    private fun showDatePicker() {
        calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(requireContext(), { _, year, month, day_of_month ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day_of_month
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            addMileageDialogBinding.etTravelDate.setText(sdf.format(calendar.time))
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        //dialog.datePicker.minDate = calendar.timeInMillis
        //calendar.add(Calendar.YEAR, -18)
        dialog.datePicker.maxDate = calendar.timeInMillis
        dialog.show()
    }

    private fun addMileage() {
        addMileageDialogBinding = AddMileageDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = getWindowHeight()
        }
        addMileageDialogBinding.apply {
            etTravelDate.keyListener = null
            etTravelDate.requestFocus()
            etTravelDate.setOnClickListener {
                showDatePicker()
            }
            cbEngineOil.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> engineOil.value = 1
                    else -> engineOil.value = 0
                }
            }
            cbTyrePressure.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> tyrePressure.value = 1
                    else -> tyrePressure.value = 0
                }
            }
            cbBreakLight.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> breakLight.value = 1
                    else -> breakLight.value = 0
                }
            }
            cbCarJack.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> carJack.value = 1
                    else -> carJack.value = 0
                }
            }
            cbCarBreakdownSign.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> breakDownSign.value = 1
                    else -> breakDownSign.value = 0
                }
            }
            cbWater.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> water.value = 1
                    else -> water.value = 0
                }
            }
            btnContinue.setOnClickListener {
                when {
                    etTravelDate.text.toString().isEmpty() ->
                        toastyErrors("Fill in the Travel Date")
                    etFrom.text.toString().isEmpty() ->
                        toastyErrors("Fill in From")
                    etTo.text.toString().isEmpty() ->
                        toastyErrors("Fill in To")
                    etStartMileage.text.toString().isEmpty() ->
                        toastyErrors("Fill in the Start Mileage")
                    etVehicleRegNo.text.toString().isEmpty() ->
                        toastyErrors("Fill in the Vehicle Reg. No")
                    else -> {
                        val addMileageDTO = breakLight.value?.let { it1 ->
                            carJack.value?.let { it2 ->
                                engineOil.value?.let { it3 ->
                                    breakDownSign.value?.let { it4 ->
                                        tyrePressure.value?.let { it5 ->
                                            water.value?.let { it6 ->
                                                AddMileageDTO(
                                                    it1,
                                                    etFrom.text.toString().capitalizeWords,
                                                    it2,
                                                    it3,
                                                    etStartMileage.text.toString(),
                                                    etTo.text.toString().capitalizeWords,
                                                    etTravelDate.text.toString(),
                                                    it4,
                                                    it5,
                                                    it6,
                                                    etVehicleRegNo.text.toString()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Log.d("TAG", "addMileage: ${Gson().toJson(addMileageDTO)}")
                        if (addMileageDTO != null) {
                            viewmodel.addMileage(addMileageDTO)
                        }

                    }
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            dialog.hide()
                            viewmodel.stopObserving()
                            toastySuccess("You have added mileage successfully")
                            findNavController().navigateUp()
                        }
                        0 -> {
                            if ("${viewmodel.statusMessage.value}".contains("failed to connect")) {
                                toastyErrors("Check your internet connection and try again")
                            } else {
                                onInfoDialogWarn(viewmodel.statusMessage.value)
                            }
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseSta.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            addMileageDialogBinding.progressbar.mainPBar.makeVisible()
                            addMileageDialogBinding.progressbar.tvWait.text = "Adding mileage..."
                        }
                        GeneralResponseStatus.DONE -> {
                            addMileageDialogBinding.progressbar.mainPBar.makeGone()
                            dialog.dismiss()
                        }
                        GeneralResponseStatus.ERROR -> {
                            addMileageDialogBinding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
        }
        dialog.setContentView(addMileageDialogBinding.root)
        dialog.show()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MileageFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int, mileage: Mileage) {
        editMileage(mileage)
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun editMileage(mileage: Mileage) {
        editMileageDialogBinding = EditMileageDialogBinding.inflate(layoutInflater)
        val editMileageDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme).apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = getWindowHeight()
            }
        editMileageDialogBinding.apply {
            etTravelDate.apply {
                keyListener = null
                setText(mileage.travel_date)
            }
            etFrom.apply {
                keyListener = null
                etFrom.setText(mileage.from)
            }
            etTo.apply {
                keyListener = null
                setText(mileage.to)
            }
            etStartMileage.apply {
                keyListener = null
                setText(mileage.start_mileage)
            }
            etEndMileage.apply {
                setText(mileage.final_mileage ?: "")
            }
            etVehicleRegNo.apply {
                setText(mileage.vehicle_reg_no ?: "")
            }
            cbEngineOil.apply {
                cbEngineOil.isEnabled = false
                when (mileage.oil_is_ok) {
                    1 -> cbEngineOil.isChecked = true
                    else -> cbEngineOil.isChecked = false
                }
            }
            cbTyrePressure.apply {
                cbTyrePressure.isEnabled = false
                when (mileage.tyre_pressure_is_ok) {
                    1 -> cbTyrePressure.isChecked = true
                    else -> cbTyrePressure.isChecked = false
                }
            }
            cbBreakLight.apply {
                cbBreakLight.isEnabled = false
                when (mileage.break_lights_are_ok) {
                    1 -> cbBreakLight.isChecked = true
                    else -> cbBreakLight.isChecked = false
                }
            }
            cbCarJack.apply {
                cbCarJack.isEnabled = false
                when (mileage.jack_is_available) {
                    1 -> cbCarJack.isChecked = true
                    else -> cbCarJack.isChecked = false
                }
            }
            cbCarBreakdownSign.apply {
                cbCarBreakdownSign.isEnabled = false
                when (mileage.triangle_is_available) {
                    1 -> cbCarBreakdownSign.isChecked = true
                    else -> cbCarBreakdownSign.isChecked = false
                }
            }
            cbWater.apply {
                cbWater.isEnabled = false
                when (mileage.water_is_ok) {
                    1 -> cbWater.isChecked = true
                    else -> cbWater.isChecked = false
                }
            }
            btnContinue.setOnClickListener {
                when {
                    etEndMileage.text.toString().isEmpty() ->
                        toastyErrors("Fill in the End Mileage")
                    else -> {
                        val updateMileageDTO =
                            UpdateMileageDTO(etEndMileage.text.toString(), mileage.id, 1)
                        viewmodel.updateMileage(updateMileageDTO)

                    }
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            viewmodel.stopObserving()
                            toastySuccess("You have updated mileage successfully")
                            editMileageDialog.dismiss()
                            findNavController().navigateUp()
                        }
                        0 -> {
                            if ("${viewmodel.statusMessage.value}".contains("failed to connect")) {
                                toastyErrors("Check your internet connection and try again")
                            } else {
                                onInfoDialogWarn(viewmodel.statusMessage.value)
                            }
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseSta.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            editMileageDialogBinding.progressbar.mainPBar.makeVisible()
                            editMileageDialogBinding.progressbar.tvWait.text = "Updating mileage..."
                        }
                        GeneralResponseStatus.DONE -> {
                            editMileageDialogBinding.progressbar.mainPBar.makeGone()
                            editMileageDialog.dismiss()
                        }
                        GeneralResponseStatus.ERROR -> {
                            editMileageDialogBinding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
        }
        editMileageDialog.setContentView(editMileageDialogBinding.root)
        editMileageDialog.show()
    }
}