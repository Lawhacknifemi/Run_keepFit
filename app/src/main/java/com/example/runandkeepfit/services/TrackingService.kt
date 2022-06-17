package com.example.runandkeepfit.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runandkeepfit.R
import com.example.runandkeepfit.others.Constants.ACTION_PAUSE_SERVICE
import com.example.runandkeepfit.others.Constants.ACTION_SHOW_TRACKING_INTENT
import com.example.runandkeepfit.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runandkeepfit.others.Constants.ACTION_STOP_SERVICE
import com.example.runandkeepfit.others.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.example.runandkeepfit.others.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runandkeepfit.others.Constants.NOTIFICATION_CHANEL_ID
import com.example.runandkeepfit.others.Constants.NOTIFICATION_CHANEL_NAME
import com.example.runandkeepfit.others.Constants.NOTIFICATION_ID
import com.example.runandkeepfit.others.Constants.TIME_UPDATE_INTERVAL
import com.example.runandkeepfit.others.TrackingUtility
import com.example.runandkeepfit.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias polyLine = MutableList<LatLng>
typealias polyLines = MutableList<polyLine>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true
    var isServicKilled = false

    @Inject
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    private var timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder : NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder


    companion object{
        val timeRunInMilliS = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathsPoints = MutableLiveData<polyLines>()
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
          updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    private fun killService(){
        isServicKilled = true
        isFirstRun = true
        pauseService()
        postInitialValue()
        stopForeground(true)
        stopSelf()
    }

    private var isTimeEnabled = true
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L


    private fun startTimer(){
        addEmptyPolyLine()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimeEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
//                Difference in time between now and Time Started
                lapTime = System.currentTimeMillis() - timeStarted
//                post new Time Lap
                timeRunInMilliS.postValue(timeRun + lapTime)
                if (timeRunInMilliS.value!! >= lastSecondTimeStamp +1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIME_UPDATE_INTERVAL)

            }
            timeRun += lapTime
        }
    }


    private fun postInitialValue(){
        isTracking.postValue(false)
        pathsPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMilliS.postValue(0L)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun){
                        startForeGroundService()
                        isFirstRun = false
                    }else{
                        Timber.d("Resuming Services....")
                        startTimer()
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused Service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped Service")
                    killService()
                }
            }


        }
        return super.onStartCommand(intent, flags, startId)

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking :Boolean){
        if(isTracking){
           if(TrackingUtility.hasLocationPermission(this)){
               val request = LocationRequest().apply {
                interval = LOCATION_UPDATE_INTERVAL
                fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                priority = PRIORITY_HIGH_ACCURACY
            }
               fusedLocationProviderClient.requestLocationUpdates(
                       request,
                       locationCallback,
                       Looper.getMainLooper()
               )
            }else{
               fusedLocationProviderClient.removeLocationUpdates(locationCallback)
           }
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!){
                result?.locations?.let {
                    locations->
                    for (location in locations){
                        addPathPoints(location)
                        Timber.d("New Location is : ${location.longitude}, ${location.latitude}")
                    }

                }
            }
        }
    }

    private fun addPathPoints(location:Location?) {
        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathsPoints.value?.apply {
                last().add(pos)
                pathsPoints.postValue(this)

            }
        }

    }

    private fun addEmptyPolyLine() = pathsPoints.value?.apply {
        add(mutableListOf())
        pathsPoints.postValue(this)
    }?: pathsPoints.postValue(mutableListOf(mutableListOf()))

    private fun pauseService(){
        isTracking.postValue(false)
        isTimeEnabled= false
    }

    private fun updateNotificationTrackingState(isTracking: Boolean){
         val notificationActionText = if(isTracking) "Pause" else "Resume"
         val pendingIntent = if (isTracking){
             val pauseIntent = Intent(this,TrackingService::class.java).apply {
                 action = ACTION_PAUSE_SERVICE
             }
             PendingIntent.getService(this,1, pauseIntent, FLAG_IMMUTABLE  or FLAG_UPDATE_CURRENT)
         }else{
             val resumeIntent = Intent(this,TrackingService::class.java).apply {
                 action = ACTION_START_OR_RESUME_SERVICE
             }
             PendingIntent.getService(this,2,resumeIntent, FLAG_IMMUTABLE  or FLAG_UPDATE_CURRENT)
         }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager


        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>()
            )
        }

        if (!isServicKilled){
            curNotificationBuilder = baseNotificationBuilder.
            addAction(R.drawable.ic_pause_black_24dp,notificationActionText,pendingIntent)

            notificationManager.notify(NOTIFICATION_ID,curNotificationBuilder.build())


        }





    }


    private fun startForeGroundService(){
        startTimer()
        addEmptyPolyLine()

        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChanel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            if(!isServicKilled){
                val notification = curNotificationBuilder
                        .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }

        })
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel(notificationManager: NotificationManager){
        val chanel = NotificationChannel(NOTIFICATION_CHANEL_ID, NOTIFICATION_CHANEL_NAME,IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(chanel)

    }

}