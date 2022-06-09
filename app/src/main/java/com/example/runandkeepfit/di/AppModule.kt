package com.example.runandkeepfit.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runandkeepfit.db.RunningDatabase
import com.example.runandkeepfit.others.Constants.FIRST_TIME_TOGGLE
import com.example.runandkeepfit.others.Constants.KEY_NAME
import com.example.runandkeepfit.others.Constants.KEY_WEIGHT
import com.example.runandkeepfit.others.Constants.RUNNING_DATABASE_NAME
import com.example.runandkeepfit.others.Constants.SHARED_PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()


    @Singleton
    @Provides
    fun providerRunDao(db :RunningDatabase) = db.getRunDao()
    
    @Singleton
    @Provides
    fun ProvideSharedPrefrences( @ApplicationContext app :Context)
            = app.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharePref : SharedPreferences) = sharePref.getString(KEY_NAME,"")?: ""

    @Singleton
    @Provides
    fun provideWeight(sharePref : SharedPreferences) = sharePref.getFloat(KEY_WEIGHT,60f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences)
            = sharedPref.getBoolean(FIRST_TIME_TOGGLE, true)

}
