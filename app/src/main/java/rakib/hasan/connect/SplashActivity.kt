package rakib.hasan.connect

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import rakib.hasan.connect.databinding.ActivitySplashBinding
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(auth.currentUser != null)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                 else startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
        }, 3000)

    }
}