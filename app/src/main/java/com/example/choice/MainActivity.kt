package com.example.choice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_bottom_nav.*
/* Main activity inits variables and redirects users if already logged in and vice versa.
Uses fragment nav for login screen
 */


//Daniel Kathiresan
class MainActivity : AppCompatActivity(), FragmentNavigation{

    private lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fAuth = Firebase.auth

        //Enables automatic login to app, does not use a checkbox (remember me)
        val currentUser = fAuth.currentUser
        if(currentUser != null){
            val intent = Intent(this@MainActivity, BottomNav::class.java)
            intent.putExtra("reLog", "reLog")
            startActivity(intent)


        }else {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, LoginFragment())
                .commit()
        }
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,fragment)

        if(addToStack){
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

}