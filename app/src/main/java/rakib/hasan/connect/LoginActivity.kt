package rakib.hasan.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import rakib.hasan.connect.databinding.ActivityLoginBinding
import rakib.hasan.connect.models.User

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onResume() {
        super.onResume()
        binding.loadingLAV.frame = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val data = it.data
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
                    // do whatever with the data in the callback
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                    authWithGoogle(account.idToken)
                }
            }

        binding.signInButtonCV.setOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

    }

    private fun authWithGoogle(idToken: String?){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val user = task.result.user
                    if (user != null) {
                        Log.e("PROFILE", user.photoUrl.toString())
                        val firebaseUser = User()
                        firebaseUser.userId = user.uid
                        firebaseUser.name = user.displayName
                        firebaseUser.profile = user.photoUrl.toString()
                        firebaseUser.city = "Unknown"
                        database.reference
                            .child("profiles")
                            .child(user.uid)
                            .setValue(firebaseUser)
                            .addOnCompleteListener {
                                if (task.isSuccessful){
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finishAffinity()
                                } else {
                                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Log.e("PROFILE", task.exception?.localizedMessage ?: "Null")
                }
            }
    }

}