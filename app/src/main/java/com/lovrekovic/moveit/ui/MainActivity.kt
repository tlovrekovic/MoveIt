package com.lovrekovic.moveit.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackingFragmentIfNeeded(intent)


        //glavniToolbar je ovaj
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)



        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment, R.id.statisticsFragment, R.id.runFragment ->
                    bottomNavigationView.visibility = View.VISIBLE
                else -> bottomNavigationView.visibility = View.GONE
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }
    private fun navigateToTrackingFragmentIfNeeded( intent: Intent?){
        if(intent?.action== ACTION_SHOW_TRACKING_FRAGMENT) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

}