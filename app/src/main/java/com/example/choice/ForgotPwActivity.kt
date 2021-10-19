package com.example.choice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_pw.*

class ForgotPwActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pw)

        back_image2.setOnClickListener {
            finish()
        }


        Send_button.setOnClickListener {
            val email: String = Email_edit_text.text.toString().trim{ it <= ' '}
            if(email.isEmpty()){
                Toast.makeText(this@ForgotPwActivity, "Please enter email address.", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(this@ForgotPwActivity, "Email sent successfully to reset password! ", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this@ForgotPwActivity, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}