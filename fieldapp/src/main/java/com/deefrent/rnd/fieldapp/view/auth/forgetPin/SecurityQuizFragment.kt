package com.deefrent.rnd.fieldapp.view.auth.forgetPin

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.PinResetDialogBinding
import com.deefrent.rnd.fieldapp.databinding.SecurityQuizFragmentBinding
import com.deefrent.rnd.fieldapp.dtos.VerifySecQuizDTO
import com.deefrent.rnd.fieldapp.network.models.SetSecurityQuizData
import com.deefrent.rnd.fieldapp.utils.*

class SecurityQuizFragment : Fragment() {
    private lateinit var binding: SecurityQuizFragmentBinding
    private lateinit var viewModel: ForgetPinViewModel
    private lateinit var cardBinding: PinResetDialogBinding

    private var q1rName=""
    private var q1Id=-1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= SecurityQuizFragmentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(ForgetPinViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnClickListener { findNavController().navigateUp() }
            val username= AppPreferences.getPreferences(requireContext(),"usernamef").toString()
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
            viewModel.secQuizData.observe(viewLifecycleOwner) {q1List->
                Log.d("TAG","VALUE$q1List")
                if (q1List!=null){
                    populateQuiz1(q1List)
                }else{
                    toastyErrors("No security question at the moment")
                }
            }
            btnContinue.setOnClickListener {
                if (isNetwork(requireContext())){
                val ans1 = etQ1.text.toString()
                    if (q1rName.isEmpty()) {
                        toastyErrors("Select security question to continue")
                    } else if (ans1.isEmpty()) {
                        AQ1.error = getString(R.string.required)
                    } else {
                    tlQ1.error = ""
                    btnContinue.isEnabled=false
                    progressr.mainPBar.makeVisible()
                    val verifySecQuizDTO= VerifySecQuizDTO()
                    verifySecQuizDTO.securityQuestionId=q1Id
                    verifySecQuizDTO.answer=ans1
                    verifySecQuizDTO.username=username
                    viewModel.verifySecurityQuiz(verifySecQuizDTO)
                }
            }else{
                onNoNetworkDialog(requireContext())
            }
        }
            viewModel.statusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    progressr.mainPBar.makeGone()
                    when (it) {
                        1 -> {
                            btnContinue.isEnabled=true
                            progressr.mainPBar.makeGone()
                            showResetDialog()
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
            spinnerQ1.setOnItemClickListener { parent, _, position, _ ->
                val selected: SetSecurityQuizData = parent.adapter.getItem(position) as SetSecurityQuizData
                q1rName=selected.name
                q1Id= selected.id
            }

        }
    }
    private fun showResetDialog(){
        val dialog = Dialog(requireContext())
        cardBinding =
            PinResetDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.btnREGISTER.setOnClickListener {
            dialog.dismiss()
            viewModel.stopObserving()
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }


}