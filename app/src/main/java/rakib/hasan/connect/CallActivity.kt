package rakib.hasan.connect

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import rakib.hasan.connect.databinding.ActivityCallBinding
import rakib.hasan.connect.models.User
import java.util.*


class CallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallBinding

    var uuId = ""

    var auth: FirebaseAuth? = null
    var username = ""
    var friendsUsername = ""

    var isPeerConnected = false

    var firebaseRef: DatabaseReference? = null

    var isAudio = true
    var isVideo = true
    var createdBy: String? = null

    var pageExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().reference.child("users")

        username = intent.getStringExtra("userId")!!
        val incoming = intent.getStringExtra("incoming")
        createdBy = intent.getStringExtra("createdBy")

//        friendsUsername = "";
//
//        if(incoming.equalsIgnoreCase(friendsUsername))
//            friendsUsername = incoming;


//        friendsUsername = "";
//
//        if(incoming.equalsIgnoreCase(friendsUsername))
//        friendsUsername = incoming;
        friendsUsername = incoming!!

        setupWebView()

        binding.micBtn.setOnClickListener(View.OnClickListener {
            isAudio = !isAudio
            callJavaScriptFunction("javascript:toggleAudio(\"$isAudio\")")
            if (isAudio) {
                binding.micBtn.setImageResource(R.drawable.btn_unmute_normal)
            } else {
                binding.micBtn.setImageResource(R.drawable.btn_mute_normal)
            }
        })

        binding.videoBtn.setOnClickListener(View.OnClickListener {
            isVideo = !isVideo
            callJavaScriptFunction("javascript:toggleVideo(\"$isVideo\")")
            if (isVideo) {
                binding.videoBtn.setImageResource(R.drawable.btn_video_normal)
            } else {
                binding.videoBtn.setImageResource(R.drawable.btn_video_muted)
            }
        })

        binding.endCall.setOnClickListener(View.OnClickListener { finish() })

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.addJavascriptInterface(InterfaceJava(this), "Android")
        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        binding.webView.loadUrl(filePath)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                initializePeer()
            }
        }
    }


    fun initializePeer() {
        uuId = getUUID()
        callJavaScriptFunction("javascript:init(\"$uuId\")")
        if (createdBy.equals(username, ignoreCase = true)) {
            if (pageExit) return
            firebaseRef!!.child(username).child("connId").setValue(uuId)
            firebaseRef!!.child(username).child("isAvailable").setValue(true)
            binding.loadingGroup.visibility = View.GONE
            binding.controls.visibility = View.VISIBLE
            FirebaseDatabase.getInstance().reference
                .child("profiles")
                .child(friendsUsername)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user: User? = snapshot.getValue(User::class.java)
                        Picasso.get().load(user?.profile)
                            .into(binding.profile)
                        binding.name.text = user?.name
                        binding.city.text = user?.city
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        } else {
            Handler().postDelayed({
                friendsUsername = createdBy!!
                FirebaseDatabase.getInstance().reference
                    .child("profiles")
                    .child(friendsUsername)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user: User? = snapshot.getValue(User::class.java)
                            Picasso.get().load(user?.profile)
                                .into(binding.profile)
                            binding.name.text = user?.name
                            binding.city.text = user?.city
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(friendsUsername)
                    .child("connId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value != null) {
                                sendCallRequest()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }, 3000)
        }
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }

    fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(
                this,
                "You are not connected. Please check your internet.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        listenConnId()
    }

    private fun listenConnId() {
        firebaseRef!!.child(friendsUsername).child("connId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) return

                    binding.loadingGroup.visibility = View.GONE
                    binding.controls.visibility = View.VISIBLE
                    val connId = snapshot.getValue(String::class.java)
                    callJavaScriptFunction("javascript:startCall(\"$connId\")")
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun callJavaScriptFunction(function: String?) {
        binding.webView.post(Runnable {
            if (function != null) {
                binding.webView.evaluateJavascript(function, null)
            }
        })
    }

    private fun getUUID(): String = UUID.randomUUID().toString()

    override fun onDestroy() {
        super.onDestroy()
        pageExit = true
        firebaseRef!!.child(createdBy!!).setValue(null)
        finish()
    }

}