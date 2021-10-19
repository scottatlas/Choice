package com.example.choice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.ContentView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom_nav.*

class BottomNav : AppCompatActivity() {

    private var fragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        replaceFragment(MainScreen())



        bottomChip.setOnItemSelectedListener { id ->
            when (id){
                R.id.btnHome -> {
                    fragment = MainScreen()
                }
                R.id.btnMap -> {
                    fragment = MapScreen()
                }
                R.id.btnFriends -> {
                    fragment = FriendsFragment()
                }
                R.id.btnSettings -> {
                    fragment = SettingFragment()
                }
            }

            fragment!!.let {
                supportFragmentManager.beginTransaction().replace(R.id.dashboardContainer, fragment!!).commit()
            }
        }
    }


    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.dashboardContainer, fragment).commit()
    }
}