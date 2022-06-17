package com.example.runandkeepfit.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.runandkeepfit.R
import com.example.runandkeepfit.others.Constants
import com.example.runandkeepfit.others.Constants.NOTIFICATION_CHANEL_ID
import com.example.runandkeepfit.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app: Context)
            = FusedLocationProviderClient(app)


    @RequiresApi(Build.VERSION_CODES.M)
    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext app:Context) = PendingIntent.getActivity(
            app,
            0,
            Intent(app, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_INTENT
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )


    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
            @ApplicationContext app:Context,
            pendingIntent: PendingIntent
    ) =  NotificationCompat.Builder(app, NOTIFICATION_CHANEL_ID)
    .setAutoCancel(false)
    .setOngoing(true)
    .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
    .setContentTitle("Tracking Your Run..")
    .setContentText("00:00:00")
    .setContentIntent(pendingIntent)

}