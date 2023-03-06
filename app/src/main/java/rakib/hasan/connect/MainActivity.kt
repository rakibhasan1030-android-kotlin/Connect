package rakib.hasan.connect

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import rakib.hasan.connect.databinding.ActivityMainBinding
import rakib.hasan.connect.utils.PermissionManager
import rakib.hasan.connect.utils.PermissionManager.PermissionAskListener


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private val REQUEST_CODE = 13
    private lateinit var permissionManager: PermissionManager
    val permissions = mutableListOf<String>()
    val cameraPermission: String = android.Manifest.permission.CAMERA
    val recordAudioPermission: String = android.Manifest.permission.RECORD_AUDIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionManager = PermissionManager(this)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        Picasso.get()
            .load(currentUser.photoUrl)
            .into(binding.profileImage)
        binding.userNameTV.text = currentUser.displayName

        binding.findButtonCV.setOnClickListener {
            askForMultiplePermissions()
        }

    }

    private fun askForMultiplePermissions() {
        permissions.clear()

        if (!hasPermission(cameraPermission)) {
            permissions.add(cameraPermission)
            Log.v("PERMISSION", "HAS CAMERA PERMISSION : ${hasPermission(cameraPermission)}")
        }
        if (!hasPermission(recordAudioPermission)) {
            permissions.add(recordAudioPermission)
            Log.v("PERMISSION", "HAS AUDIO PERMISSION : ${hasPermission(cameraPermission)}")
        }
        if ((permissions.isNotEmpty() && !hasPermission(recordAudioPermission)) ||
            (permissions.isNotEmpty() && !hasPermission(recordAudioPermission))) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE)

            permissionManager.checkPermission(context = this, permissions = permissions, listener = object : PermissionAskListener {

                override fun onNeedPermission(permission: String) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(permission),
                        REQUEST_CODE
                    )
                }

                override fun onPermissionPreviouslyDenied(permission: String) {

                    if (permission == cameraPermission){
                        showRationalDialog(
                            getString(R.string.permission_denied),
                            getString(R.string.rational_dialog_message_camera),
                            permission
                        )
                    }
                    if (permission == recordAudioPermission){
                        showRationalDialog(
                            getString(R.string.permission_denied),
                            getString(R.string.rational_dialog_message_audio),
                            permission,
                        )
                    }
                }

                override fun onPermissionPreviouslyDeniedWithNeverAskAgain(permission: String) {
                    showSettingsDialog(
                        getString(R.string.permission_denied),
                        getString(R.string.setting_dialog_message),
                    )
                }

                override fun onPermissionGranted(permission: String) {

                }
            })
        }
        else startActivity(Intent(this@MainActivity, MatchingActivity::class.java))

    }


    private fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.parse("package:$packageName")
        intent.data = uri
        startActivity(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun showRationalDialog(title: String, msg: String, permission: String) {
        AlertDialog.Builder(this@MainActivity).setTitle(title)
            .setMessage(msg)
            .setCancelable(false)
            .setNegativeButton("I'M SURE", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .setPositiveButton("RETRY", DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    REQUEST_CODE
                )
                dialog.dismiss()
            }).show()
    }

    private fun showSettingsDialog(title: String, msg: String) {
        AlertDialog.Builder(this@MainActivity).setTitle(title).setMessage(msg)
            .setCancelable(false)
            .setNegativeButton("NOT NOW",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .setPositiveButton("SETTINGS", DialogInterface.OnClickListener { dialog, which ->
                goToSettings()
                dialog.dismiss()
            }).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.v("PERMISSIONS", "REQUEST CODE: ${requestCode.toString()}\n}")

        permissions.forEachIndexed { index, permission ->
            Log.v("PERMISSIONS", "permissions[$index] : ${permission.toString()}\n")
        }
        grantResults.forEachIndexed { index, grantResult ->
            Log.v("PERMISSIONS", "grantResults[$index] : ${grantResult.toString()}\n")
        }

        when (requestCode) {
            REQUEST_CODE -> {
                if (hasPermission(cameraPermission) && hasPermission(recordAudioPermission)){
                    startActivity(Intent(this@MainActivity, MatchingActivity::class.java))
                    Toast.makeText(this@MainActivity, "${permissions.toString()} -> ${getString(R.string.permission_granted)}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun hasPermission(permission: String?): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }

}



































