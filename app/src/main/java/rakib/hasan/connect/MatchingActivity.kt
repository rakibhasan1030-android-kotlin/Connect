package rakib.hasan.connect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import rakib.hasan.connect.databinding.ActivityMatchingBinding
import java.util.*

class MatchingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var uid: String
    var isOkay = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        currentUser = auth.currentUser!!
        uid = currentUser.uid
        Picasso.get()
            .load(currentUser.photoUrl)
            .into(binding.profileImage);

        database.reference.child("users")
            .orderByChild("status")
            .equalTo(0.toDouble())
            .limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.childrenCount > 0){
                        // Room available
                        isOkay = true

                        for (snap in snapshot.children) {
                            database.reference
                                .child("users")
                                .child(snap.key ?: "")
                                .child("incoming")
                                .setValue(uid)

                            database.reference
                                .child("users")
                                .child(snap.key ?: "")
                                .child("status")
                                .setValue(1)

                            startActivity(
                                Intent(this@MatchingActivity, CallActivity::class.java)
                                    .putExtra("userId", uid)
                                    .putExtra("incoming", snap.child("incoming").value.toString())
                                    .putExtra("createdBy", snap.child("createdBy").value.toString())
                                    .putExtra("isAvailable", snap.child("isAvailable").value as Boolean)
                            )

                        }

                    } else {
                        // Room not available, create new ROOM
                        val room : HashMap<String, Any> = hashMapOf()
                        room["incoming"] = uid
                        room["createdBy"] = uid
                        room["isAvailable"] = true
                        room["status"] = 0
                        database.reference
                            .child("users")
                            .child(uid)
                            .setValue(room)
                            .addOnSuccessListener {
                                database.reference
                                    .child("users")
                                    .child(uid)
                                    .addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.child("status").exists()){
                                                if (snapshot.child("status").value.toString() == "1"){
                                                    if (isOkay) return

                                                    isOkay = true
                                                    startActivity(
                                                        Intent(this@MatchingActivity, CallActivity::class.java)
                                                            .putExtra("userId", uid)
                                                            .putExtra("incoming", snapshot.child("incoming").value.toString())
                                                            .putExtra("createdBy", snapshot.child("createdBy").value.toString())
                                                            .putExtra("isAvailable", snapshot.child("isAvailable").value as Boolean)
                                                    )
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }
}