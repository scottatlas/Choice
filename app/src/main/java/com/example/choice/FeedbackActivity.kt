package com.example.choice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private var firebaseUserID : String = ""
    private lateinit var database: DatabaseReference
    private lateinit var FeedbackText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        fAuth = Firebase.auth
        firebaseUserID = fAuth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("Users")

        Feedback_send_button.setOnClickListener {

            val FeedbackText: String = Feedback_edit_text.text.toString()

            if(FeedbackText.isEmpty()){
                Toast.makeText(this@FeedbackActivity,"Please enter your feedback", Toast.LENGTH_SHORT).show()
            }else{
                database.child(firebaseUserID).child("feedback").setValue(FeedbackText).addOnSuccessListener {
                    Toast.makeText(this@FeedbackActivity,"Thank your for your feedback!",Toast.LENGTH_LONG).show()
                }
            }
        }

        Feedback_back_iamge.setOnClickListener {
            finish()
        }

    }
}