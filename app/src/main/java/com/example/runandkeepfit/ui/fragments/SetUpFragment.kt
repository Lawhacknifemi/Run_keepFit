package com.example.runandkeepfit.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runandkeepfit.R
import com.example.runandkeepfit.databinding.FragmentSetupBinding
import com.example.runandkeepfit.others.Constants.FIRST_TIME_TOGGLE
import com.example.runandkeepfit.others.Constants.KEY_NAME
import com.example.runandkeepfit.others.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment :Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref:SharedPreferences

    @set:Inject
    var isFirstTimeOpen = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSetupBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSetupBinding.bind(view)

        if (!isFirstTimeOpen){
            val navOption = NavOptions.Builder()
                    .setPopUpTo(R.id.setUpFragment,true)
                    .build()
            findNavController().navigate(R.id.action_setUpFragment_to_runFragment,savedInstanceState,navOption)

        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success){
                findNavController().navigate(R.id.action_setUpFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(),"Please Enter All Fields",Snackbar.LENGTH_SHORT).show()

            }

        }

    }

    private fun writePersonalDataToSharedPref(): Boolean{
         val name = view!!.findViewById<TextView>(R.id.etName).text.toString()
        val weight = view!!.findViewById<TextView>(R.id.etWeight).text.toString()
        if (name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedPref.edit()
                .putString(KEY_NAME,name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(FIRST_TIME_TOGGLE, false)
                .apply()
        val toolbarText = "Let's Go $name"
        requireActivity().findViewById<TextView>(R.id.tvToolbarTitle).text = toolbarText
        return true


    }
}