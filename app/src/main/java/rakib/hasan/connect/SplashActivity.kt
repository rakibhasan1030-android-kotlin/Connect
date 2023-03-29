package rakib.hasan.connect

import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
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

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val distanceToMove = -screenWidth - binding.sloganTv.width
        val animation = TranslateAnimation(screenWidth, distanceToMove, 0F, 0F)
        animation.duration = 10000 // Set the duration of the animation in milliseconds
        animation.interpolator = LinearInterpolator() // Set the interpolator to LinearInterpolator to move the text at a constant speed
        animation.repeatCount = Animation.INFINITE // Set the animation to repeat infinitely
        binding.sloganTv.startAnimation(animation)



        Timer().schedule(object : TimerTask() {
            override fun run() {
                /*if(auth.currentUser != null)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                 else startActivity(Intent(this@SplashActivity, LoginActivity::class.java))*/
            }
        }, 3000)

    }
}