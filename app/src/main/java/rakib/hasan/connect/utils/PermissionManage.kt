package rakib.hasan.connect.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class PermissionManager(context: Context) {
    private val context: Context
    private val sessionManager: SessionManager

    init {
        this.context = context
        sessionManager = SessionManager(context)
    }

    private fun shouldAskPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun shouldAskPermission(context: Context, permission: String): Boolean {
        if (shouldAskPermission()) {
            val permissionResult = ActivityCompat.checkSelfPermission(context, permission)
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    fun checkPermission(context: Context, permissions: MutableList<String>, listener: PermissionAskListener) {
        permissions.forEach { permission ->
            if (shouldAskPermission(context, permission)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as AppCompatActivity),
                        permission
                    )
                ) {
                    listener.onPermissionPreviouslyDenied(permission)
                } else {
                    if (sessionManager.isFirstTimeAsking(permission)) {
                        sessionManager.firstTimeAsking(permission, false)
                        listener.onNeedPermission(permission)
                    } else {
                        listener.onPermissionPreviouslyDeniedWithNeverAskAgain(permission)
                    }
                }
            } else {
                listener.onPermissionGranted(permission)
            }
        }
    }

    interface PermissionAskListener {
        fun onNeedPermission(permission: String)
        fun onPermissionPreviouslyDenied(permission: String)
        fun onPermissionPreviouslyDeniedWithNeverAskAgain(permission: String)
        fun onPermissionGranted(permission: String)
    }

}