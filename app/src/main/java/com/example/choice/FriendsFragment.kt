package com.example.choice

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//Daniel Kathiresan Rewrite
class FriendsFragment : Fragment() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()

    //On view creation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        //Define references
        userDB = Firebase.database.reference.child("Users")
        //Create arraylist
        var view =  inflater.inflate(R.layout.fragment_friends, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        getMatchUsers()
        return view
    }

    private fun getMatchUsers() {
        val matchedDB = userDB.child(getCurrentUserID()).child("likedBy").child("match")

        matchedDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key?.isNotEmpty() == true) {
                    getUserKey(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getUserKey(uid: String) {
        // add users to user list
        userDB.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(uid != getCurrentUserID()){
                    cardItems.add(CardItem(uid, snapshot.child("first_name").value.toString(), snapshot.child("bio").value.toString(), snapshot.child("profile_picture").value.toString()) )
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
                else{
                    println("Current UID matching, not including")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            println("Null user")
        }
        return auth.currentUser?.uid.orEmpty()
    }
}