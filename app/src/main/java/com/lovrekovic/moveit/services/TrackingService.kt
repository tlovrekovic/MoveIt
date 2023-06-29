package com.lovrekovic.moveit.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.Constants.ACTION_PAUSE_SERVICE
import com.lovrekovic.moveit.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.lovrekovic.moveit.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.lovrekovic.moveit.other.Constants.ACTION_STOP_SERVICE
import com.lovrekovic.moveit.other.Constants.FASTEST_LOCATION_INTERVAL
import com.lovrekovic.moveit.other.Constants.LOCATION_UPDATE_INTERVAL
import com.lovrekovic.moveit.other.Constants.NOTIFICATION_CHANNEL_ID
import com.lovrekovic.moveit.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.lovrekovic.moveit.other.Constants.NOTIFICATION_ID
import com.lovrekovic.moveit.other.Constants.TIMER_UPDATE_INTERVAL
import com.lovrekovic.moveit.other.TrackingUtility
import com.lovrekovic.moveit.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

typealias  Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

//notifikacija za foreground servis, ako je background moze se prekinuti nenadano
// foreground kao activity
//nasljeđivanjem možemo raći LiveData observe f.ji u kojem ciklusu je servis
class TrackingService : LifecycleService() {


    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()
    companion object {
        val timeRunInMillis = MutableLiveData<Long> ()
        val isTracking = MutableLiveData<Boolean>()

        //lista polyline
        val pathPoints = MutableLiveData<Polylines>()
    }

    override fun onCreate() {
        super.onCreate()
        postInititalValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    private fun postInititalValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                    }else{
                        Timber.d("Resuming service...")
                        startTimer()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    pauseService()
                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        //pomaknuto par ms dok god pratimo izvodi se while loop
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //time difference
                lapTime = System.currentTimeMillis() - timeStarted
                //post new laptime
                timeRunInMillis.postValue(timeRun+lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimestamp +1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! +1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }

    }
    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
               val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL).apply {
                   setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                   setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                   setWaitForAccurateLocation(false)
               }.build()
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    
    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let {locations ->
                    for(location in locations){
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }

    }

    private fun  addPathPoint(location: Location?){
        //provjera da nije null
        location?.let {
            val pos= LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() =  pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))

    // notificationManager je system service koji nam treba kad god trebamo prikazati obavjest, notifikacija se lokalno stvara
    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager

        createNotoficationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
        startForeground(NOTIFICATION_ID,notificationBuilder.build())
        Timber.d("notificationBuilderWorking")
    }
    //za ovo definiram globalnu akciju
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also{
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    //low jer se svaku sekundu mijenja notifikacija i ne zelimo da mobitel zvoni svaku sekundu
    private fun createNotoficationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(channel)
        Timber.d("CREATEnotifChannelWorking")


    }

}