package com.deefrent.rnd.fieldapp.view.auth.userlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentSetSecurityQuizBinding
import com.deefrent.rnd.fieldapp.dtos.SetSecQuizDTO
import com.deefrent.rnd.fieldapp.network.models.SetSecurityQuizData
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.ForgetPinViewModel
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.utils.*

class SetSecurityQuizFragment : Fragment() {
    private lateinit var binding: FragmentSetSecurityQuizBinding
    private lateinit var viewModel: ForgetPinViewModel
    private var q1rName=""
    private var q1Id=""
    private var q2rName=""
    private var q2Id=""
    private var q3rName=""
    private var q3Id=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSetSecurityQuizBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(ForgetPinViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnClickListener { findNavController().navigateUp() }
            viewModel.secQuizData.observe(viewLifecycleOwner){q1List->
                if (q1List!=null){
                    populateQuiz1(q1List)
                    populateQuiz2(q1List)
                    populateQuiz3(q1List)
                }else{
                    toastyErrors("No security question at the moment")
                }
            }
            viewModel.responseStatus.observe(viewLifecycleOwner){
                if (null!=it){
                    when(it){
                        GeneralResponseStatus.LOADING->{
                            btnContinue.isEnabled=false
                            progressr.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE->{
                            btnContinue.isEnabled=true
                            progressr.mainPBar.makeGone()
                        }
                        else->{
                            btnContinue.isEnabled=true
                            progressr.mainPBar.makeGone()
                        }
                    }
                }
            }
            etQ1.hint=getString(R.string.give_your_answers_here)
            etQ2.hint=getString(R.string.give_your_answers_here)
            etQ3.hint=getString(R.string.give_your_answers_here)
            btnContinue.setOnClickListener {
                if (isNetwork(requireContext())) {
                    val ans1 = etQ1.text.toString()
                    val ans2 = etQ2.text.toString()
                    val ans3 = etQ3.text.toString()
                    if (q1rName.isEmpty()) {
                        toastyErrors("Select Question 1")
                    } else if (ans1.isEmpty()) {
                        AQ1.error = getString(R.string.required)
                    } else if (q2rName.isEmpty()) {
                        toastyErrors("Select Question 2")
                    } else if (ans2.isEmpty()) {
                        AQ2.error = getString(R.string.required)
                    } else if (q3rName.isEmpty()) {
                        toastyErrors("Select Question 3")
                    } else if (ans3.isEmpty()) {
                        AQ3.error = getString(R.string.required)
                    } else {
                        AQ1.error = ""
                        AQ2.error = ""
                        AQ3.error = ""
                        progressr.mainPBar.makeVisible()
                        btnContinue.isEnabled = false
                        val setSecQuizDTO = SetSecQuizDTO()
                        setSecQuizDTO.security_question_one_id = q1Id
                        setSecQuizDTO.security_question_two_id = q2Id
                        setSecQuizDTO.security_question_three_id = q3Id
                        setSecQuizDTO.security_question_one_answer = ans1
                        setSecQuizDTO.security_question_two_answer = ans2
                        setSecQuizDTO.security_question_three_answer = ans3
                        viewModel.saveSecurityQuiz(setSecQuizDTO)
                    }
                }else{
                    onNoNetworkDialog(requireContext())
                }

            }
            viewModel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    progressr.mainPBar.makeGone()
                    when (it) {
                        1 -> {
                            btnContinue.isEnabled=true
                            progressr.mainPBar.makeGone()
                            viewModel.stopObserving()
                        }
                        0 -> {
                            btnContinue.isEnabled=true
                            onInfoDialog(viewModel.statusMessage.value )
                            progressr.mainPBar.makeGone()
                            viewModel.stopObserving()

                        }
                        else -> {
                            btnContinue.isEnabled=true
                            onInfoDialog(getString(R.string.error_occurred))
                            progressr.mainPBar.makeGone()
                            viewModel.stopObserving()
                        }
                    }
                }
            }



        }

    }
    private fun populateQuiz1(genderList: List<SetSecurityQuizData>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout, genderList)
        binding.apply {
            spinnerQ1.setAdapter(typeAdapter)
            spinnerQ1.keyListener = null
            /*spinnerQ1.setText(genderList[0].name)
            tlQ1.isFocusable=true*/
            spinnerQ1.setOnItemClickListener { parent, _, position, _ ->
                val selected: SetSecurityQuizData = parent.adapter.getItem(position) as SetSecurityQuizData
                q1rName=selected.name
                q1Id= selected.id.toString()
            }

        }
    }
    private fun populateQuiz2(genderList: List<SetSecurityQuizData>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.apply {
            spinnerQ2.setAdapter(typeAdapter)
            spinnerQ2.keyListener = null
            spinnerQ2.setOnItemClickListener { parent, _, position, _ ->
                val selected: SetSecurityQuizData = parent.adapter.getItem(position) as SetSecurityQuizData
                q2rName=selected.name
                q2Id= selected.id.toString()
                AQ2.makeVisible()
            }
        }
    }
    private fun populateQuiz3(genderList: List<SetSecurityQuizData>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.apply {
            spinnerQ3.setAdapter(typeAdapter)
            spinnerQ3.keyListener = null
            spinnerQ3.setOnItemClickListener { parent, _, position, _ ->
                val selected: SetSecurityQuizData = parent.adapter.getItem(position) as SetSecurityQuizData
                q3rName=selected.name
                q3Id= selected.id.toString()
                AQ3.makeVisible()
            }
        }
    }

}