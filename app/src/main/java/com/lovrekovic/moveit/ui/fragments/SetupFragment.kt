package com.lovrekovic.moveit.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.lovrekovic.moveit.other.Constants.KEY_NAME
import com.lovrekovic.moveit.other.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @set:Inject
    var isFirstAppOpen = true


    lateinit var etName : EditText
    lateinit var tvToolbarTitle : TextView
    lateinit var etWeight : EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvContinue: TextView = view.findViewById(R.id.tvContinue)
        etName = view.findViewById(R.id.etName)
        etWeight = view.findViewById(R.id.etWeight)
        //tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle)

        if(!isFirstAppOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment, savedInstanceState, navOptions)
        }
        tvContinue.setOnClickListener{
            val sucess = writePersonalDataToSharedPref()
            if(sucess){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)

            }else{
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean{
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"
        //tvToolbarTitle.text= toolbarText
        return true
    }
}