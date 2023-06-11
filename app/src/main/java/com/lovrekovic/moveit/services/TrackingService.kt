package com.lovrekovic.moveit.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.Constants.ACTION_PAUSE_SERVICE
import com.lovrekovic.moveit.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.lovrekovic.moveit.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.lovrekovic.moveit.other.Constants.ACTION_STOP_SERVICE
import com.lovrekovic.moveit.other.Constants.NOTIFICATION_CHANNEL_ID
import com.lovrekovic.moveit.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.lovrekovic.moveit.other.Constants.NOTIFICATION_ID
import com.lovrekovic.moveit.ui.MainActivity
import timber.log.Timber

//notifikacija za foreground servis, ako je background moze se prekinuti nenadano
// foreground kao activity
//nasljeđivanjem možemo raći LiveData observe f.ji u kojem ciklusu je servis
class TrackingService : LifecycleService() {


    var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                    }else{
                        Timber.d("Resuming service...")
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // notificationManager je system service koji nam treba kad god trebamo prikazati obavjest, notifikacija se lokalno stvara
    private fun startForegroundService() {
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