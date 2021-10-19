package com.example.choice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_logout.*



class LogoutActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        back_image.setOnClickListener {
            finish()
        }

        Feedbacj_button.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

        Notify_button.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        Logout2_button.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}