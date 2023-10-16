package com.deefrent.rnd.fieldapp.ui.main.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.BuildConfig
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentLoginBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.viewModels.AuthViewModel
import es.dmoral.toasty.Toasty
import javax.inject.Inject

class LoginFragment : BaseDaggerFragment() {
    //private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val loginSessionSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)
            .get(LoginSessionSharedViewModel::class.java)
    }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerFieldAppComponent.builder()
            .appComponent((requireActivity().application as BaseApp).appComponent)
            .build().inject(this)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("dash", "onCreateView: ${this::class.java.simpleName}")
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedPreferences =
            requireContext().getSharedPreferences("accessToken", Context.MODE_PRIVATE)
        binding.btnSignIn.setOnClickListener { v ->
            if (validateInput()) {
                if (isNetworkAvailable(requireContext())) {
                    loginUser(v)
                } else {
                    showNoInternetDialog()
                }
            }
        }
        return view
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet")
        builder.setIcon(R.drawable.ic_info)
        builder.setMessage(
            "You are offline. Please connect to the internet to login"
        )

        builder.setPositiveButton("OK") { dialog, which ->
            //callDialog("Saving data locally...", requireContext(), v)
        }
        builder.show()
    }

    private fun loginUser(v: View) {
        Constants.callDialog2("Signing in...", requireContext())
        authViewModel.loginUser(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString(),
            BuildConfig.GRANT_TYPE
        ).observe(viewLifecycleOwner) { loginUserResponse ->
            if (loginUserResponse != null) {
                val accessToken = loginUserResponse.access_token
                /*val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@observe
                with (sharedPref.edit()) {
                    putString("access_token", accessToken)
                    apply()
                }*/
                loginSessionSharedViewModel.setIsFromLoginScreen(true)
                /*val editor = sharedPreferences!!.edit()
                editor.putString("token", accessToken).apply()*/
                AppPreferences.token = accessToken
                Toasty.success(requireContext(), "Sign in Successful", Toasty.LENGTH_SHORT).show()
                Constants.cancelDialog()
                v.findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            } else {
                Constants.cancelDialog()
                Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInput(): Boolean {
        val isInputValid: Boolean
        when (binding.etEmail.text.toString().isEmpty() && binding.etPassword.text.toString()
            .isEmpty()) {
            true -> {
                isInputValid = false
                Toasty.error(requireContext(), "Please fill in all details", Toasty.LENGTH_LONG)
                    .show()
            }
            else -> isInputValid = true
        }
        return isInputValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}