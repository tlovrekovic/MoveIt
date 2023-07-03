package com.lovrekovic.moveit.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.Constants.KEY_NAME
import com.lovrekovic.moveit.other.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {
   lateinit var etWeight : EditText
   lateinit var etName : EditText
   //lateinit var tvToolbarTitle : TextView

    @Inject
    lateinit var  sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPreferences()
        val btnApplyChanges : Button = view.findViewById(R.id.btnApplyChanges)
        btnApplyChanges.setOnClickListener{
            val success = applyChangesToSharedPref()
            if(success){
                Snackbar.make(view,"Saved changes", Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(view,"Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loadFieldsFromSharedPreferences(){
        etName  = requireView().findViewById(R.id.etName)
        etWeight = requireView().findViewById(R.id.etWeight)
        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean{
        //tvToolbarTitle= requireView().findViewById(R.id.tvToolbarTitle)
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go $nameText"
        //tvToolbarTitle.text = toolbarText
        return true
    }
}