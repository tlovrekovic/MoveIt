package com.lovrekovic.moveit.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.PolylineOptions
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.Constants.ACTION_PAUSE_SERVICE
import com.lovrekovic.moveit.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.lovrekovic.moveit.other.Constants.MAP_ZOOM
import com.lovrekovic.moveit.other.Constants.POLYLINE_COLOR
import com.lovrekovic.moveit.other.Constants.POLYLINE_WIDTH
import com.lovrekovic.moveit.other.TrackingUtility
import com.lovrekovic.moveit.services.Polyline
import com.lovrekovic.moveit.services.TrackingService
import com.lovrekovic.moveit.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {

    private val viewModel : MainViewModel by viewModels()
    private lateinit var mapView: MapView
    private var isTracking=false
    private var pathPoints = mutableListOf<Polyline>()
    private var map: GoogleMap? = null
    private var currentTimeMillis = 0L




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        val btnToggleRun : Button =  view.findViewById(R.id.btnToggleRun)
        btnToggleRun.setOnClickListener{
            toggleRun()
        }

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        //kad se pozove znamo da nova lokacija postoji u service
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            val tvTimer : TextView = requireView().findViewById(R.id.tvTimer)
            currentTimeMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeMillis,true)
            tvTimer.text = formattedTime
        })
    }

    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)

        }
    }

    private fun updateTracking(isTracking:Boolean){
        val btnToggleRun = view?.findViewById<Button>(R.id.btnToggleRun)
        val btnFinishRun = view?.findViewById<Button>(R.id.btnFinishRun)
        this.isTracking=isTracking
        if(!isTracking){
            btnToggleRun?.text="Start"
            btnFinishRun?.visibility = View.VISIBLE
        }else{
            btnToggleRun?.text="Stop"
            btnFinishRun?.visibility = View.VISIBLE
        }

    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }
    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }
    private fun  addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size >1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size -2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService (action:String)=
        Intent(requireContext(), TrackingService::class.java).also{
            it.action=action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //keširanje mape
        mapView?.onSaveInstanceState(outState)
    }


}