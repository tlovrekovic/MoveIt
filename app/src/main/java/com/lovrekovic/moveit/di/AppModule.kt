package com.lovrekovic.moveit.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.lovrekovic.moveit.database.RunDatabase
import com.lovrekovic.moveit.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.lovrekovic.moveit.other.Constants.KEY_NAME
import com.lovrekovic.moveit.other.Constants.KEY_WEIGHT
import com.lovrekovic.moveit.other.Constants.RUN_DATABASE_NAME
import com.lovrekovic.moveit.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
//govorimo androidstudio da installira modul u singletoncomponent
//ovisnosti postoje samo kad se izvodi
//zive samo koliko activity zivi, samo jedna instanca
@InstallIn(SingletonComponent::class)
object AppModule {
    //rezultat f.je moze stvoriti nove ovisnosti i moze se ubrizgati u klase
    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext app:Context)
    = Room.databaseBuilder(
        app,
        RunDatabase::class.java,
        RUN_DATABASE_NAME
    ).build()

    //definiramo funkcije ali ne pozivamo ih DAgger to radi sam
    @Singleton
    @Provides
    fun provideRunDAO(db:RunDatabase)=db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences)= sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight (sharedPref: SharedPreferences)= sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences)= sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE,true)
}