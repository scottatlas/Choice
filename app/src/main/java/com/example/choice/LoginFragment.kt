package com.example.choice

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//Daniel Kathiresan
class LoginFragment : Fragment() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var logBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)

        email = view.findViewById(R.id.login_email)
        password = view.findViewById(R.id.login_password)
        fAuth = Firebase.auth
        logBtn = view.findViewById(R.id.loginButton)

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)

        }

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            //Run validation function when called
            validateForm()

        }

        view.findViewById<TextView>(R.id.ForgotPw_text).setOnClickListener {
            val intent = Intent (activity, ForgotPwActivity::class.java)
            startActivity(intent)
        }

        return view
    }


    private fun firebaseSignIn(){
        logBtn.isEnabled = false
        logBtn.alpha = 0.5f
        fAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                val intent = Intent (activity, BottomNav::class.java)
                activity?.startActivity(intent)
//                var navHome = activity as FragmentNavigation
//                navHome.navigateFrag(MatchFragment(), addToStack = true)
            }else{
                logBtn.isEnabled = true
                logBtn.alpha = 1.0f
                Toast.makeText(context, task.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(){
        //Show warning icon (currently placeholder) when a criteria is met
        val icon = AppCompatResources.getDrawable(requireContext(),
            R.drawable.warningph)

        icon?.setBounds(0, 0,icon.intrinsicWidth,icon.intrinsicHeight)
        //check string User has entered
        when{
            TextUtils.isEmpty((email.text.toString().trim()))->{

                email.setError("Please Enter an Email Address",icon)
            }
            TextUtils.isEmpty((password.text.toString().trim()))->{

                password.setError("Please Enter Password",icon)
            }

            email.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() ->
            {//Currently checking if username is equal to email,will change to email later
                if (email.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                    //Correct if matches format, then check password
                    if(password.text.toString().length>=5){
                        //If more than 5 characters check confirm password
                        //Login with firebase
                        firebaseSignIn()
                    }else{
                        password.setError("Please enter at least 5 characters",icon)
                    }
                }else{
                    email.setError("Please Enter Valid Email Id",icon)
                }

            }
        }
    }
}