package co.tpcreative.presentation.common.services

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject

class NetworkConnectivityHelper @Inject constructor(context: Context) {

    private val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nc = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            nc != null
                    && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}