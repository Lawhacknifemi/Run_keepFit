package com.example.runandkeepfit.others

import android.graphics.Color

object Constants {
    const val RUNNING_DATABASE_NAME = "running_db"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val ACTION_START_OR_RESUME_SERVICE = "START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_INTENT = "ACTION_SHOW_TRACKING_INTENT"

    const val TIME_UPDATE_INTERVAL = 50L


    const val CANCEL_TRACKING_DIALOG_TAG = "CancelDialog"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

    const val SHARED_PREFERENCE_NAME ="sharedPref"
    const val FIRST_TIME_TOGGLE = "FIRST_TIME_TOGGLE"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"


    const val POLY_LINE_COLOR = Color.RED
    const val POLY_LINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val NOTIFICATION_CHANEL_ID = "NOTIFICATION_CHANEL_ID"
    const val NOTIFICATION_CHANEL_NAME = "tracking_chanel"
    const val NOTIFICATION_ID = 1
}