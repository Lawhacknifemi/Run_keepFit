package com.example.runandkeepfit.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.runandkeepfit.R
import com.example.runandkeepfit.databinding.FragmentSettingsBinding
import com.example.runandkeepfit.databinding.FragmentSetupBinding
import com.example.runandkeepfit.databinding.FragmentTrackingBinding
import com.example.runandkeepfit.others.Constants.KEY_NAME
import com.example.runandkeepfit.others.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment :Fragment(R.layout.fragment_settings) {
    lateinit var binding : FragmentSettingsBinding

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return FragmentSettingsBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         binding = FragmentSettingsBinding.bind(view)
        val success = applyChangesToSharedPref()
        loadFieldFromSharePref()
        binding.btnApplyChanges.setOnClickListener{
            if (success){
            Snackbar.make(view,"Changes Saved!",Snackbar.LENGTH_LONG).show()
        }else{
            Snackbar.make(view,"Please Fill out all Fields",Snackbar.LENGTH_SHORT).show()
        }
        }

    }

    private fun loadFieldFromSharePref(){
        val name = sharedPref.getString(KEY_NAME, "")
        val weight = sharedPref.getFloat(KEY_WEIGHT, 60f)
        this.binding.etName.setText(name)
        this.binding.etWeight.setText(weight.toString())

    }

    private fun applyChangesToSharedPref():Boolean{
        val nameText = this.binding.etName.text.toString()
        val weightText = this.binding.etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPref.edit()
                .putString(KEY_NAME, nameText)
                .putFloat(KEY_WEIGHT, weightText.toFloat())
                .apply()
        val toolbarText = "Let's Go $nameText"
        requireActivity().findViewById<TextView>(R.id.tvToolbarTitle)
                .text = toolbarText
        return true
    }
}