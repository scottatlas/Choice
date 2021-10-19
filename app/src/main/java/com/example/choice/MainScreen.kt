package com.example.choice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
//Daniel Kathiresan Main Screen
class MainScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_main_screen, container, false)


        view.findViewById<Button>(R.id.GoMatchBtn).setOnClickListener {
            //Open Match Screen
            val intent = Intent (activity, MatchActivity::class.java)
            activity?.startActivity(intent)
        }

        return view
    }
}