package com.deefrent.rnd.fieldapp.view.homepage.myDiary

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.DiaryAdapter
import com.deefrent.rnd.fieldapp.databinding.AddDiaryDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditDiaryDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentMyDiaryBinding
import com.deefrent.rnd.fieldapp.dtos.AddDiaryDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateDiaryDTO
import com.deefrent.rnd.fieldapp.network.models.DiaryList
import com.deefrent.rnd.fieldapp.network.models.EventType
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.DiaryCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.ListDiaryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class MyDiaryFragment : Fragment(), DiaryCallBack, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentMyDiaryBinding
    private lateinit var addDiaryDialogBinding: AddDiaryDialogBinding
    private lateinit var editDiaryDialogBinding: EditDiaryDialogBinding
    private var eventId by Delegates.notNull<Int>()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(ListDiaryViewModel::class.java)
    }
    private lateinit var diaryAdapter: DiaryAdapter
    private var arrayList: ArrayList<DiaryList> = arrayListOf()
    private var displayList: ArrayList<DiaryList> = arrayListOf()
    private lateinit var calendar: Calendar
    private var day by Delegates.notNull<Int>()
    private var month by Delegates.notNull<Int>()
    private var year by Delegates.notNull<Int>()
    private var hour by Delegates.notNull<Int>()
    private var minute by Delegates.notNull<Int>()
    private var myday by Delegates.notNull<Int>()
    private var myMonth by Delegates.notNull<Int>()
    private var myYear by Delegates.notNull<Int>()
    private var myHour by Delegates.notNull<Int>()
    private var myMinute by Delegates.notNull<Int>()
    private var isEdit by Delegates.notNull<Boolean>()
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
        binding = FragmentMyDiaryBinding.inflate(layoutInflater)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        diaryAdapter = DiaryAdapter(displayList, this)
        binding.rvDiary.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvDiary.adapter = diaryAdapter
        viewmodel.getMyDiary()
        getMyDiary()
        binding.btnRefresh.setOnClickListener {
            getMyDiary()
        }
        binding.rvDiary.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.btnAddMileage.isShown) {
                    super.onScrolled(recyclerView, dx, dy)
                    binding.btnAddMileage.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.btnAddMileage.show()
                }
            }
        })
        return binding.root
    }

    private fun getMyDiary() {
        if (isNetworkAvailable(requireContext())) {
            binding.rvDiary.makeGone()
            binding.noMileage.makeGone()
            binding.btnAddMileage.makeGone()
            binding.noNewRequest.makeGone()
            binding.btnRefresh.makeGone()
            observeViewModel()
        } else {
            binding.rvDiary.makeGone()
            binding.noMileage.makeGone()
            binding.btnAddMileage.makeGone()
            binding.noNewRequest.makeVisible()
            binding.btnRefresh.makeVisible()
            binding.noNewRequest.text = "Please check your internet connection and try again!"
        }
    }

    private fun observeViewModel() {
        binding.rvDiary.makeGone()
        binding.noNewRequest.makeGone()
        binding.btnRefresh.makeGone()
        viewmodel.diaryData.observe(viewLifecycleOwner) {
            displayList.clear()
            displayList.addAll(it.data.diaryList)
            diaryAdapter.notifyDataSetChanged()
            if (it.data.diaryList.isEmpty()) {
                binding.rvDiary.makeVisible()
                binding.noMileage.makeVisible()
                binding.btnAddMileage.makeVisible()
                binding.noNewRequest.makeGone()
                binding.noNewRequest.text = "You have not added anything to your diary"
                binding.btnRefresh.makeGone()
            } else {
                binding.noNewRequest.makeGone()
                binding.noMileage.makeGone()
                binding.rvDiary.makeVisible()
                binding.btnAddMileage.makeVisible()
                binding.btnRefresh.makeGone()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddMileage.setOnClickListener { addDiary() }
        viewmodel.responseStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.rvDiary.makeGone()
                        binding.btnAddMileage.makeGone()
                        binding.noNewRequest.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Fetching your diary..."
                    }
                    GeneralResponseStatus.DONE -> {
                        binding.rvDiary.makeVisible()
                        binding.btnAddMileage.makeVisible()
                        binding.noNewRequest.makeGone()
                        binding.btnRefresh.makeGone()
                        binding.progressbar.mainPBar.makeGone()
                    }
                    GeneralResponseStatus.ERROR -> {
                        binding.rvDiary.makeGone()
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
                            displayList.addAll(diary.data.diaryList)
                            diaryAdapter.notifyDataSetChanged()
                            if (diary.data.diaryList.isEmpty()) {
                                binding.noMileage.makeVisible()
                                binding.rvDiary.makeGone()
                                binding.btnAddMileage.makeVisible()
                                binding.noNewRequest.makeGone()
                                binding.noNewRequest.text =
                                    "You have not added anything to your diary"
                                binding.btnRefresh.makeGone()
                            } else {
                                binding.noMileage.makeGone()
                                binding.noNewRequest.makeGone()
                                binding.rvDiary.makeVisible()
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
                        binding.rvDiary.makeGone()
                        binding.btnAddMileage.makeGone()
                        binding.noMileage.makeGone()
                    }
                    else -> {
                        viewmodel.stopObserving()
                        binding.rvDiary.makeGone()
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

    private fun addDiary() {
        isEdit = false
        addDiaryDialogBinding = AddDiaryDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        addDiaryDialogBinding.apply {
            etDate.setOnClickListener { pickDate() }
            viewmodel.diaryData.observe(viewLifecycleOwner) { diary ->
                populateEventType(diary.data.eventTypes)
            }
            btnContinue.setOnClickListener {
                when {
                    spType.text.toString().isEmpty() ->
                        toastyErrors("Select the Event Type")
                    etDate.text.toString().isEmpty() ->
                        toastyErrors("Select the Date and Time")
                    etVenue.text.toString().isEmpty() ->
                        toastyErrors("Enter the event Venue")
                    else -> {
                        val addDiaryDTO = AddDiaryDTO()
                        addDiaryDTO.description = etDesc.text.toString()
                        addDiaryDTO.event_date = etDate.text.toString()
                        addDiaryDTO.event_type_id = eventId.toString()
                        addDiaryDTO.is_active = 1
                        addDiaryDTO.venue = etVenue.text.toString()
                        viewmodel.addDiary(addDiaryDTO)
                    }
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            dialog.dismiss()
                            findNavController().navigateUp()
                            viewmodel.stopObserving()
                            toastySuccess("You have added your diary successfully")
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
                            addDiaryDialogBinding.progressbar.mainPBar.makeVisible()
                            addDiaryDialogBinding.progressbar.tvWait.text =
                                "Submitting your diary..."
                        }
                        GeneralResponseStatus.DONE -> {
                            dialog.dismiss()
                            addDiaryDialogBinding.progressbar.mainPBar.makeGone()
                        }
                        GeneralResponseStatus.ERROR -> {
                            addDiaryDialogBinding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
        }
        dialog.setContentView(addDiaryDialogBinding.root)
        dialog.show()
    }

    private fun editDiary(diary: DiaryList) {
        isEdit = true
        editDiaryDialogBinding = EditDiaryDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        editDiaryDialogBinding.apply {
            etDate.setOnClickListener { pickDate() }
            viewmodel.diaryData.observe(viewLifecycleOwner) { diaryData ->
                populateEventTypeEdit(diaryData.data.eventTypes, diary)
            }
            etDate.setText(diary.eventDate)
            etVenue.setText(diary.venue)
            etDesc.setText(diary.description ?: "")
            btnContinue.setOnClickListener {
                when {
                    spType.text.toString().isEmpty() ->
                        toastyErrors("Select the Event Type")
                    etDate.text.toString().isEmpty() ->
                        toastyErrors("Select the Date and Time")
                    etVenue.text.toString().isEmpty() ->
                        toastyErrors("Enter the event Venue")
                    else -> {
                        val updateDiaryDTO = UpdateDiaryDTO(
                            etDesc.text.toString(), etDate.text.toString(),
                            eventId, diary.id, diary.isActive, etVenue.text.toString()
                        )
                        viewmodel.editDiary(updateDiaryDTO)
                    }
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            dialog.dismiss()
                            findNavController().navigateUp()
                            viewmodel.stopObserving()
                            toastySuccess("You have updated your diary successfully")
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
                            editDiaryDialogBinding.progressbar.mainPBar.makeVisible()
                            editDiaryDialogBinding.progressbar.tvWait.text =
                                "Updating your diary..."
                        }
                        GeneralResponseStatus.DONE -> {
                            dialog.dismiss()
                            editDiaryDialogBinding.progressbar.mainPBar.makeGone()
                        }
                        GeneralResponseStatus.ERROR -> {
                            editDiaryDialogBinding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }


        }
        dialog.setContentView(editDiaryDialogBinding.root)
        dialog.show()
    }

    private fun populateEventType(rship: List<EventType>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        addDiaryDialogBinding.spType.setAdapter(typeAdapter)
        addDiaryDialogBinding.spType.keyListener = null
        addDiaryDialogBinding.spType.setOnItemClickListener { parent, _, position, _ ->
            val selected: EventType = parent.adapter.getItem(position) as EventType
            addDiaryDialogBinding.spType.setText(selected.name, false)
            eventId = selected.id
        }
    }

    private fun populateEventTypeEdit(rship: List<EventType>, diaryItem: DiaryList) {
        editDiaryDialogBinding.spType.setText(diaryItem.eventTypeName)
        eventId = diaryItem.id
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        editDiaryDialogBinding.spType.setAdapter(typeAdapter)
        editDiaryDialogBinding.spType.keyListener = null
        editDiaryDialogBinding.spType.setOnItemClickListener { parent, _, position, _ ->
            val selected: EventType = parent.adapter.getItem(position) as EventType
            editDiaryDialogBinding.spType.setText(selected.name, false)
            eventId = selected.id
        }
    }

    private fun pickDate() {
        calendar = Calendar.getInstance()
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog =
            DatePickerDialog(requireContext(), this@MyDiaryFragment, year, month, day)
        //datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun showDatePicker() {
        calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(requireContext(), { _, year, month, day_of_month ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day_of_month
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            addDiaryDialogBinding.etDate.setText(sdf.format(calendar.time))
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        //dialog.datePicker.minDate = calendar.timeInMillis
        //calendar.add(Calendar.YEAR, -18)
        dialog.datePicker.maxDate = calendar.timeInMillis
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyDiaryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemSelected(pos: Int, diary: DiaryList) {
        editDiary(diary)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myYear = year
        myday = day
        myMonth = month
        val c = Calendar.getInstance()
        hour = c[Calendar.HOUR]
        minute = c[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            this@MyDiaryFragment,
            hour,
            minute, true
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        when (isEdit) {
            true -> editDiaryDialogBinding.etDate.setText("$myYear-${myMonth + 1}-$myday $myHour:$myMinute:00")
            false -> addDiaryDialogBinding.etDate.setText("$myYear-${myMonth + 1}-$myday $myHour:$myMinute:00")
        }
    }
}