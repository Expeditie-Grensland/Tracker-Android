package nl.expeditiegrensland.tracker.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.*
import nl.expeditiegrensland.tracker.BuildConfig
import nl.expeditiegrensland.tracker.R
import nl.expeditiegrensland.tracker.helpers.PreferenceHelper
import nl.expeditiegrensland.tracker.layout.main.MainActivity

class LocationUpdateService : Service() {
    companion object {
        private const val UPDATE_INTERVAL: Long = 2000 // in ms, inexact
        private const val UPDATE_INTERVAL_FAST: Long = 1000

        private val TAG = LocationUpdateService::class.java.simpleName
        private const val CHANNEL_ID = "channel_01"
        private const val NOTIFICATION_ID = 1313131

        const val ACTION_BROADCAST = "${BuildConfig.APPLICATION_ID}.broadcast"
        const val EXTRA_LOCATION = "${BuildConfig.APPLICATION_ID}.location"
    }

    private var isChangingConfiguration = false
    private val binder = MyBinder()
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback
    private lateinit var handler: Handler
    private var location: Location? = null

    override fun onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.let {
                    onNewLocation(it.lastLocation)
                }
            }
        }

        locationRequest = LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(UPDATE_INTERVAL_FAST)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Started")
        return START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        isChangingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "Binding")
        stopForeground(true)
        isChangingConfiguration = false
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "Rebinding")
        stopForeground(true)
        isChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Unbinding")

        if (!isChangingConfiguration && PreferenceHelper.getIsRequestingUpdates(this)) {
            Log.i(TAG, "Upgrading to foreground")
            startForeground(NOTIFICATION_ID, getNotification())
        }

        return true
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
    }

    fun setLocationUpdates(requesting: Boolean) {
        try {
            PreferenceHelper.setIsRequestingUpdates(this, requesting)
            if (requesting) {
                Log.i(TAG, "Starting location updates")
                startService(Intent(applicationContext, LocationUpdateService::class.java))
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
            } else {
                Log.i(TAG, "Stopping location updates")
                fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
                stopSelf()
            }
        } catch (err: SecurityException) {
            Log.e(TAG, "Lost location permission. $err")
            PreferenceHelper.setIsRequestingUpdates(this, !requesting)
        }

    }

    private fun getNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(
                    PendingIntent.getActivity(this, 0,
                            Intent(this, MainActivity::class.java), 0)
            )
            .setContentTitle("Titel")
            .setContentText("Tekst")
            .setOngoing(true)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())
            .build()

    private fun getLastLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null)
                    location = task.result
                else
                    Log.w(TAG, "Failed to get location.")
            }
        } catch (err: SecurityException) {
            Log.e(TAG, "Lost location permission. $err")
        }
    }

    private fun onNewLocation(location: Location) {
        this.location = location

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(
                Intent(ACTION_BROADCAST).putExtra(EXTRA_LOCATION, location)
        )

        if (isServiceRunningInForeground())
            notificationManager.notify(NOTIFICATION_ID, getNotification())
    }

    inner class MyBinder : Binder() {
        fun getService() = this@LocationUpdateService
    }

    private fun isServiceRunningInForeground(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (javaClass.name == service.service.className && service.foreground)
                    return true
        }
        return false
    }
}