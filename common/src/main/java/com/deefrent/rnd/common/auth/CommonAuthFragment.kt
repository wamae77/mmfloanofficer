package com.deefrent.rnd.common.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.databinding.FragmentAuthCommonBinding
import com.deefrent.rnd.common.utils.removeNonDigits
import com.deefrent.rnd.common.utils.setToolbarTitle
import kotlinx.android.synthetic.main.fragment_auth_common.*
import kotlinx.coroutines.launch

class CommonAuthFragment : BaseCommonDIFragment<FragmentAuthCommonBinding>(
    FragmentAuthCommonBinding::inflate
) {

    private var pinAdapter: PinAdapter = PinAdapter()

    // observe this list to get the pin entered
    private var pinList = MutableLiveData(
        mutableListOf<Pin>().apply {

            // initialize it with 4 dummy digits
            repeat(4) {
                add(Pin("*"))
            }
        }
    )

/*    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAuthCommonBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setUpObserver()
    }

    private fun setUpUI() {
        binding.apply {
            // set up pin recyclerview
            recyclerView2.apply {
                pinAdapter.submitList(pinList.value)
                adapter = pinAdapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
            setToolbarTitle(
                "Authorise Transaction",
                clToolBar = binding.clToolBar,
                activity = requireActivity()
            )


            // set up keyboard
            includeKeyBoard.apply {
                btnOne.getKeyboardDigit()

                btnTwo.getKeyboardDigit()

                btnThree.getKeyboardDigit()

                btnFour.getKeyboardDigit()

                btnFive.getKeyboardDigit()

                btnSix.getKeyboardDigit()

                btnSeven.getKeyboardDigit()

                btnEight.getKeyboardDigit()

                btnNine.getKeyboardDigit()

                btnZero.getKeyboardDigit()

                // removing the last digit
                btnErase.setOnClickListener {
                    pinList.value?.asReversed()?.forEach { pin ->
                        if (pin.digit != "*") {
                            pin.digit = "*"
                            pinList.value = pinList.value
                            return@setOnClickListener
                        }
                    }
                }
            }
        }
    }

    // set up observer for the pin list
    private fun setUpObserver() {
        pinList.observe(viewLifecycleOwner) { pin ->
            pinAdapter.submitList(pin)
            pinAdapter.notifyDataSetChanged()

            if (pin.filter { it.digit == "*" }.toList().isEmpty()) {
                Log.e(
                    "",
                    "PIN========================\n ${
                        pin.toString().removeNonDigits()
                    } \n=================="
                )
                simulateLoading(pin = pin.toString().removeNonDigits())
            }
        }
    }

    private fun simulateLoading(pin: String) {
        lifecycleScope.launch {
            // simulateSearching()
            // Use the Kotlin extension in the fragment-ktx artifact
            setFragmentResult("requestKey", bundleOf(/*AuthResultTag to authResult,*/ "pin" to pin))
            findNavController().navigateUp()
        }
    }


    // get value from pressed button
    private fun Button.getKeyboardDigit() {
        setOnClickListener {
            pinList.value?.forEach { digit ->
                if (digit.digit == "*") {
                    digit.digit = text.toString()
                    pinList.value = pinList.value
                    return@setOnClickListener
                }
            }
        }
    }

    companion object {
        private const val TAG = "PinBottomSheet"
        const val AuthResultTag = "authResult"
    }
}

/*enum class AuthResult {
    AUTH_SUCCESS,
    AUTH_ERROR
}*/
